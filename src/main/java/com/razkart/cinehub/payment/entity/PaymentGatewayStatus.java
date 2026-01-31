package com.razkart.cinehub.payment.entity;

/**
 * Payment gateway transaction status.
 */
public enum PaymentGatewayStatus {
    INITIATED,
    PROCESSING,
    SUCCESS,
    FAILED,
    REFUND_INITIATED,
    REFUNDED
}
