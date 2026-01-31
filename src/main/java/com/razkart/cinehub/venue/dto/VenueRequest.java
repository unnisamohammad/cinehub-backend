package com.razkart.cinehub.venue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VenueRequest(
        @NotBlank(message = "Venue name is required")
        String name,

        @NotNull(message = "City ID is required")
        Long cityId,

        @NotBlank(message = "Address is required")
        String address,

        String landmark,

        BigDecimal latitude,

        BigDecimal longitude,

        String contactPhone,

        String contactEmail,

        String facilities
) {}
