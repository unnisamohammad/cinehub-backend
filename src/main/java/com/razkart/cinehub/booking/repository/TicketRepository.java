package com.razkart.cinehub.booking.repository;

import com.razkart.cinehub.booking.entity.Ticket;
import com.razkart.cinehub.booking.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByBookingId(Long bookingId);

    List<Ticket> findByBookingIdAndStatus(Long bookingId, TicketStatus status);
}
