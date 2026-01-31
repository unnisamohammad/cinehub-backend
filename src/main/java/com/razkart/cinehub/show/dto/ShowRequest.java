package com.razkart.cinehub.show.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ShowRequest(
        @NotNull(message = "Event ID is required")
        Long eventId,

        @NotNull(message = "Screen ID is required")
        Long screenId,

        @NotNull(message = "Show date is required")
        LocalDate showDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        @NotNull(message = "Pricing is required")
        List<PricingRequest> pricing
) {
    public record PricingRequest(
            @NotNull(message = "Seat type is required")
            String seatType,

            @NotNull(message = "Price is required")
            BigDecimal price
    ) {}
}
