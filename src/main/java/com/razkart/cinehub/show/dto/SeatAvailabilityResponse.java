package com.razkart.cinehub.show.dto;

import com.razkart.cinehub.venue.entity.Seat;
import com.razkart.cinehub.venue.entity.SeatType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record SeatAvailabilityResponse(
        Long showId,
        Integer totalSeats,
        Integer availableSeats,
        Map<String, BigDecimal> pricing,
        Map<String, List<SeatInfo>> rows
) {
    public record SeatInfo(
            Long id,
            String seatLabel,
            SeatType seatType,
            Integer xPosition,
            Integer yPosition,
            boolean isAvailable,
            BigDecimal price
    ) {}

    public static SeatAvailabilityResponse from(
            Long showId,
            List<Seat> allSeats,
            Set<Long> unavailableSeatIds,
            Map<String, BigDecimal> pricing) {

        Map<String, List<SeatInfo>> rows = allSeats.stream()
                .collect(Collectors.groupingBy(
                        Seat::getRowName,
                        Collectors.mapping(seat -> new SeatInfo(
                                seat.getId(),
                                seat.getSeatLabel(),
                                seat.getSeatType(),
                                seat.getXPosition(),
                                seat.getYPosition(),
                                seat.getIsAvailable() && !unavailableSeatIds.contains(seat.getId()),
                                pricing.getOrDefault(seat.getSeatType().name(), BigDecimal.ZERO)
                        ), Collectors.toList())
                ));

        int totalSeats = allSeats.size();
        int availableSeats = (int) allSeats.stream()
                .filter(s -> s.getIsAvailable() && !unavailableSeatIds.contains(s.getId()))
                .count();

        return new SeatAvailabilityResponse(showId, totalSeats, availableSeats, pricing, rows);
    }
}
