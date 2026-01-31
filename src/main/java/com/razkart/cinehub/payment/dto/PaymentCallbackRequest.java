package com.razkart.cinehub.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentCallbackRequest(
        @NotBlank(message = "Order ID is required")
        String orderId,

        @NotBlank(message = "Payment ID is required")
        String paymentId,

        @NotBlank(message = "Signature is required")
        String signature,

        String status,

        String errorCode,

        String errorDescription
) {}
