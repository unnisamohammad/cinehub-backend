package com.razkart.cinehub.booking.service;

import com.razkart.cinehub.booking.dto.*;
import com.razkart.cinehub.show.dto.SeatAvailabilityResponse;
import com.razkart.cinehub.user.entity.User;

import java.util.List;

/**
 * Service interface for booking operations.
 */
public interface BookingService {

    /**
     * Initiate a new booking (locks seats, calculates pricing).
     */
    BookingResponse initiateBooking(BookingRequest request, User user);

    /**
     * Confirm booking after successful payment.
     */
    BookingResponse confirmBooking(Long bookingId, PaymentConfirmation payment);

    /**
     * Cancel a booking.
     */
    BookingResponse cancelBooking(Long bookingId, Long userId, String reason);

    /**
     * Get booking details.
     */
    BookingResponse getBooking(Long bookingId, Long userId);

    /**
     * Get booking by booking number.
     */
    BookingResponse getBookingByNumber(String bookingNumber, Long userId);

    /**
     * Get all bookings for a user.
     */
    List<BookingResponse> getUserBookings(Long userId);

    /**
     * Get seat availability for a show.
     */
    SeatAvailabilityResponse getAvailableSeats(Long showId);

    /**
     * Get ticket details.
     */
    TicketResponse getTicket(String ticketNumber, Long userId);
}
