package com.razkart.cinehub.venue.dto;

import com.razkart.cinehub.venue.entity.Seat;
import com.razkart.cinehub.venue.entity.SeatType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record SeatLayoutResponse(
        Long screenId,
        String screenName,
        Integer totalSeats,
        Map<String, List<SeatInfo>> rows
) {
    public record SeatInfo(
            Long id,
            String seatLabel,
            SeatType seatType,
            Integer xPosition,
            Integer yPosition,
            Boolean isAvailable
    ) {
        public static SeatInfo from(Seat seat) {
            return new SeatInfo(
                    seat.getId(),
                    seat.getSeatLabel(),
                    seat.getSeatType(),
                    seat.getXPosition(),
                    seat.getYPosition(),
                    seat.getIsAvailable()
            );
        }
    }

    public static SeatLayoutResponse from(Long screenId, String screenName, List<Seat> seats) {
        Map<String, List<SeatInfo>> rows = seats.stream()
                .collect(Collectors.groupingBy(
                        Seat::getRowName,
                        Collectors.mapping(SeatInfo::from, Collectors.toList())
                ));

        return new SeatLayoutResponse(screenId, screenName, seats.size(), rows);
    }
}
