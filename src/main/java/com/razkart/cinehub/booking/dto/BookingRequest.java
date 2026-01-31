package com.razkart.cinehub.booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BookingRequest(
        @NotNull(message = "Show ID is required")
        Long showId,

        @NotEmpty(message = "At least one seat must be selected")
        @Size(max = 10, message = "Maximum 10 seats per booking")
        List<Long> seatIds
) {}
