package com.razkart.cinehub.booking.entity;

import com.razkart.cinehub.show.entity.Show;
import com.razkart.cinehub.venue.entity.Seat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * BookedSeat entity - junction table for booking and seats.
 */
@Entity
@Table(name = "booked_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "seat_label", nullable = false, length = 10)
    private String seatLabel;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
