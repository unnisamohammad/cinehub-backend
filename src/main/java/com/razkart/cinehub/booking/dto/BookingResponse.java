package com.razkart.cinehub.booking.dto;

import com.razkart.cinehub.booking.entity.Booking;
import com.razkart.cinehub.booking.entity.BookingStatus;
import com.razkart.cinehub.booking.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        String bookingNumber,
        Long showId,
        String eventTitle,
        String venueName,
        String screenName,
        String showDate,
        String showTime,
        BookingStatus status,
        PaymentStatus paymentStatus,
        List<SeatInfo> seats,
        PricingDetail pricing,
        LocalDateTime expiresAt,
        LocalDateTime bookedAt,
        List<TicketInfo> tickets
) {
    public record SeatInfo(
            Long seatId,
            String seatLabel,
            String seatType,
            BigDecimal price
    ) {}

    public record TicketInfo(
            String ticketNumber,
            String seatLabel,
            String status
    ) {}

    public static BookingResponse from(Booking booking) {
        List<SeatInfo> seats = booking.getBookedSeats().stream()
                .map(bs -> new SeatInfo(
                        bs.getSeat().getId(),
                        bs.getSeatLabel(),
                        bs.getSeat().getSeatType().name(),
                        bs.getPrice()
                ))
                .toList();

        List<TicketInfo> tickets = booking.getTickets().stream()
                .map(t -> new TicketInfo(
                        t.getTicketNumber(),
                        t.getSeatLabel(),
                        t.getStatus().name()
                ))
                .toList();

        PricingDetail pricing = new PricingDetail(
                booking.getTotalAmount(),
                booking.getConvenienceFee(),
                booking.getTaxAmount(),
                booking.getDiscountAmount(),
                booking.getFinalAmount()
        );

        return new BookingResponse(
                booking.getId(),
                booking.getBookingNumber(),
                booking.getShow().getId(),
                booking.getShow().getEvent().getTitle(),
                booking.getShow().getScreen().getVenue().getName(),
                booking.getShow().getScreen().getName(),
                booking.getShow().getShowDate().toString(),
                booking.getShow().getStartTime().toString(),
                booking.getStatus(),
                booking.getPaymentStatus(),
                seats,
                pricing,
                booking.getExpiresAt(),
                booking.getBookedAt(),
                tickets
        );
    }
}
