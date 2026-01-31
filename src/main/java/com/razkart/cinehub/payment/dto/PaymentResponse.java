package com.razkart.cinehub.payment.dto;

import com.razkart.cinehub.payment.entity.Payment;
import com.razkart.cinehub.payment.entity.PaymentGatewayStatus;
import com.razkart.cinehub.payment.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long bookingId,
        String bookingNumber,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String paymentGateway,
        String gatewayOrderId,
        PaymentGatewayStatus status,
        LocalDateTime initiatedAt,
        LocalDateTime completedAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getBooking().getBookingNumber(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentGateway(),
                payment.getGatewayOrderId(),
                payment.getStatus(),
                payment.getInitiatedAt(),
                payment.getCompletedAt()
        );
    }
}
