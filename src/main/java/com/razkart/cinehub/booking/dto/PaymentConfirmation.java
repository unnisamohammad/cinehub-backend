package com.razkart.cinehub.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentConfirmation(
        @NotNull(message = "Amount is required")
        BigDecimal amount,

        @NotBlank(message = "Payment ID is required")
        String paymentId,

        @NotBlank(message = "Payment method is required")
        String paymentMethod
) {}
