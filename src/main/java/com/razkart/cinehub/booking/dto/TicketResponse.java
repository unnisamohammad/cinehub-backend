package com.razkart.cinehub.booking.dto;

import com.razkart.cinehub.booking.entity.Ticket;
import com.razkart.cinehub.booking.entity.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String ticketNumber,
        String bookingNumber,
        String eventTitle,
        String venueName,
        String screenName,
        String showDate,
        String showTime,
        String seatLabel,
        String qrCode,
        TicketStatus status,
        LocalDateTime scannedAt
) {
    public static TicketResponse from(Ticket ticket) {
        var booking = ticket.getBooking();
        var show = booking.getShow();

        return new TicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                booking.getBookingNumber(),
                show.getEvent().getTitle(),
                show.getScreen().getVenue().getName(),
                show.getScreen().getName(),
                show.getShowDate().toString(),
                show.getStartTime().toString(),
                ticket.getSeatLabel(),
                ticket.getQrCode(),
                ticket.getStatus(),
                ticket.getScannedAt()
        );
    }
}
