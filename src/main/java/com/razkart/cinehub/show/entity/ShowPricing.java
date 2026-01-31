package com.razkart.cinehub.show.entity;

import com.razkart.cinehub.venue.entity.SeatType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Pricing for different seat types in a show.
 */
@Entity
@Table(name = "show_pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
