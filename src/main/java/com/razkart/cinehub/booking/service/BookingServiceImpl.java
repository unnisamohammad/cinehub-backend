package com.razkart.cinehub.booking.service;

import com.razkart.cinehub.booking.dto.*;
import com.razkart.cinehub.booking.entity.*;
import com.razkart.cinehub.booking.repository.*;
import com.razkart.cinehub.common.exception.*;
import com.razkart.cinehub.show.dto.SeatAvailabilityResponse;
import com.razkart.cinehub.show.entity.Show;
import com.razkart.cinehub.show.repository.ShowRepository;
import com.razkart.cinehub.user.entity.User;
import com.razkart.cinehub.venue.entity.Seat;
import com.razkart.cinehub.venue.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final TicketRepository ticketRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final SeatLockService seatLockService;

    @Value("${cinehub.booking.expiry-minutes:10}") private int bookingExpiryMinutes;
    @Value("${cinehub.booking.max-seats-per-booking:10}") private int maxSeatsPerBooking;
    @Value("${cinehub.booking.convenience-fee-percent:5.0}") private BigDecimal convenienceFeePercent;
    @Value("${cinehub.booking.tax-percent:18.0}") private BigDecimal taxPercent;

    @Override
    @Transactional
    public BookingResponse initiateBooking(BookingRequest request, User user) {
        log.info("Initiating booking for user: {}, show: {}", user.getId(), request.showId());

        Show show = findShow(request.showId());
        validateBookingRequest(show, request, user);

        List<Long> lockedSeats = seatLockService.lockSeats(show.getId(), request.seatIds(), user.getId(), bookingExpiryMinutes);
        if (lockedSeats.size() != request.seatIds().size()) {
            seatLockService.releaseSeatsByUser(show.getId(), user.getId());
            throw new SeatNotAvailableException("Some selected seats are no longer available");
        }

        try {
            List<Seat> seats = seatRepository.findAllById(request.seatIds());
            Map<String, BigDecimal> seatTypePrices = getPricingMap(show);
            PricingDetail pricing = calculatePricing(seats, seatTypePrices);

            Booking booking = Booking.builder()
                    .user(user).show(show)
                    .totalAmount(pricing.ticketAmount()).convenienceFee(pricing.convenienceFee())
                    .taxAmount(pricing.taxAmount()).finalAmount(pricing.finalAmount())
                    .expiresAt(LocalDateTime.now().plusMinutes(bookingExpiryMinutes))
                    .build();

            seats.forEach(seat -> booking.addSeat(createBookedSeat(show, seat, seatTypePrices)));

            Booking savedBooking = bookingRepository.save(booking);
            log.info("Booking initiated: {}", savedBooking.getBookingNumber());
            return BookingResponse.from(savedBooking);
        } catch (Exception e) {
            seatLockService.releaseSeatsByUser(show.getId(), user.getId());
            throw e;
        }
    }

    @Override
    @Transactional
    public BookingResponse confirmBooking(Long bookingId, PaymentConfirmation payment) {
        log.info("Confirming booking: {}", bookingId);

        Booking booking = findBooking(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) throw new BusinessException("Booking is not in pending state");

        if (booking.isExpired()) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            seatLockService.releaseSeatsByUser(booking.getShow().getId(), booking.getUser().getId());
            throw new BusinessException("Booking has expired");
        }

        if (payment.amount().compareTo(booking.getFinalAmount()) != 0) throw new BusinessException("Payment amount mismatch");

        booking.confirm();
        generateTickets(booking);

        Booking confirmedBooking = bookingRepository.save(booking);
        log.info("Booking confirmed: {}", confirmedBooking.getBookingNumber());
        return BookingResponse.from(confirmedBooking);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId, String reason) {
        log.info("Cancelling booking: {}", bookingId);

        Booking booking = findBookingForUser(bookingId, userId, "cancel");
        booking.cancel(reason);
        booking.getTickets().forEach(ticket -> ticket.setStatus(TicketStatus.CANCELLED));

        List<Long> seatIds = booking.getBookedSeats().stream().map(bs -> bs.getSeat().getId()).toList();
        seatLockService.releaseSeats(booking.getShow().getId(), seatIds);

        Booking cancelledBooking = bookingRepository.save(booking);
        log.info("Booking cancelled: {}", cancelledBooking.getBookingNumber());
        return BookingResponse.from(cancelledBooking);
    }

    @Override
    public BookingResponse getBooking(Long bookingId, Long userId) {
        return BookingResponse.from(findBookingForUser(bookingId, userId, "view"));
    }

    @Override
    public BookingResponse getBookingByNumber(String bookingNumber, Long userId) {
        Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingNumber));
        if (!booking.getUser().getId().equals(userId)) throw new BusinessException("Not authorized to view this booking");
        return BookingResponse.from(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(BookingResponse::from).toList();
    }

    @Override
    public SeatAvailabilityResponse getAvailableSeats(Long showId) {
        Show show = findShow(showId);
        List<Seat> allSeats = seatRepository.findByScreenId(show.getScreen().getId());

        Set<Long> unavailableSeatIds = new HashSet<>(seatLockService.getLockedSeats(showId));
        unavailableSeatIds.addAll(bookedSeatRepository.findBookedSeatIdsByShowId(showId));

        return SeatAvailabilityResponse.from(showId, allSeats, unavailableSeatIds, getPricingMap(show));
    }

    @Override
    public TicketResponse getTicket(String ticketNumber, Long userId) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketNumber));
        if (!ticket.getBooking().getUser().getId().equals(userId)) throw new BusinessException("Not authorized to view this ticket");
        return TicketResponse.from(ticket);
    }

    private Show findShow(Long showId) {
        return showRepository.findById(showId).orElseThrow(() -> new ResourceNotFoundException("Show not found: " + showId));
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
    }

    private Booking findBookingForUser(Long bookingId, Long userId, String action) {
        Booking booking = findBooking(bookingId);
        if (!booking.getUser().getId().equals(userId)) throw new BusinessException("Not authorized to " + action + " this booking");
        return booking;
    }

    private void validateBookingRequest(Show show, BookingRequest request, User user) {
        if (!show.isBookable()) throw new BusinessException("Show is not available for booking");
        if (request.seatIds().size() > maxSeatsPerBooking) throw new BusinessException("Maximum " + maxSeatsPerBooking + " seats allowed per booking");
        if (bookingRepository.existsByUserAndShowAndStatusIn(user, show, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED))) {
            throw new BusinessException("You already have a booking for this show");
        }
    }

    private BookedSeat createBookedSeat(Show show, Seat seat, Map<String, BigDecimal> seatTypePrices) {
        return BookedSeat.builder()
                .show(show).seat(seat).seatLabel(seat.getSeatLabel())
                .price(seatTypePrices.getOrDefault(seat.getSeatType().name(), BigDecimal.ZERO))
                .build();
    }

    private Map<String, BigDecimal> getPricingMap(Show show) {
        return show.getPricing().stream().collect(Collectors.toMap(sp -> sp.getSeatType().name(), sp -> sp.getPrice()));
    }

    private PricingDetail calculatePricing(List<Seat> seats, Map<String, BigDecimal> seatTypePrices) {
        BigDecimal ticketAmount = seats.stream()
                .map(seat -> seatTypePrices.getOrDefault(seat.getSeatType().name(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal convenienceFee = ticketAmount.multiply(convenienceFeePercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal subtotal = ticketAmount.add(convenienceFee);
        BigDecimal taxAmount = subtotal.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return new PricingDetail(ticketAmount, convenienceFee, taxAmount, BigDecimal.ZERO, subtotal.add(taxAmount));
    }

    private void generateTickets(Booking booking) {
        booking.getBookedSeats().forEach(seat -> booking.getTickets().add(Ticket.builder()
                .booking(booking).seatLabel(seat.getSeatLabel())
                .ticketNumber("TKT" + System.currentTimeMillis() + String.format("%06d", (int) (Math.random() * 1000000)))
                .qrCode(Base64.getEncoder().encodeToString((booking.getBookingNumber() + "|" + seat.getSeatLabel() + "|" + booking.getShow().getShowDate() + "|" + booking.getShow().getStartTime()).getBytes()))
                .status(TicketStatus.VALID).build()));
    }
}
