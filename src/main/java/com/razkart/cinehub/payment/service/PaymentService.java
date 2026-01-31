package com.razkart.cinehub.payment.service;

import com.razkart.cinehub.payment.dto.PaymentCallbackRequest;
import com.razkart.cinehub.payment.dto.PaymentRequest;
import com.razkart.cinehub.payment.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    /**
     * Initiate payment for a booking.
     */
    PaymentResponse initiatePayment(PaymentRequest request, Long userId);

    /**
     * Handle payment gateway callback.
     */
    PaymentResponse handleCallback(PaymentCallbackRequest callback);

    /**
     * Get payment details.
     */
    PaymentResponse getPayment(Long paymentId);

    /**
     * Get payments for a booking.
     */
    List<PaymentResponse> getPaymentsByBooking(Long bookingId);

    /**
     * Initiate refund for a payment.
     */
    void initiateRefund(Long paymentId, String reason);
}
