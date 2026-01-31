package com.razkart.cinehub.payment.service;

import com.razkart.cinehub.booking.entity.Booking;
import com.razkart.cinehub.booking.entity.BookingStatus;
import com.razkart.cinehub.booking.repository.BookingRepository;
import com.razkart.cinehub.booking.service.BookingService;
import com.razkart.cinehub.booking.dto.PaymentConfirmation;
import com.razkart.cinehub.common.exception.BusinessException;
import com.razkart.cinehub.common.exception.ResourceNotFoundException;
import com.razkart.cinehub.payment.dto.PaymentCallbackRequest;
import com.razkart.cinehub.payment.dto.PaymentRequest;
import com.razkart.cinehub.payment.dto.PaymentResponse;
import com.razkart.cinehub.payment.entity.*;
import com.razkart.cinehub.payment.repository.PaymentRepository;
import com.razkart.cinehub.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    private static final String PAYMENT_GATEWAY = "RAZORPAY";

    @Override
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request, Long userId) {
        log.info("Initiating payment for booking: {}", request.bookingId());

        // Check for duplicate payment using idempotency key
        if (paymentRepository.existsByIdempotencyKey(request.idempotencyKey())) {
            Payment existingPayment = paymentRepository.findByIdempotencyKey(request.idempotencyKey())
                    .orElseThrow();
            return PaymentResponse.from(existingPayment);
        }

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + request.bookingId()));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Booking is not in pending state");
        }

        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException("Not authorized to pay for this booking");
        }

        if (booking.isExpired()) {
            throw new BusinessException("Booking has expired");
        }

        // Create payment record
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getFinalAmount())
                .paymentMethod(PaymentMethod.valueOf(request.paymentMethod()))
                .paymentGateway(PAYMENT_GATEWAY)
                .gatewayOrderId(generateOrderId())
                .idempotencyKey(request.idempotencyKey())
                .status(PaymentGatewayStatus.INITIATED)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment initiated: {}", savedPayment.getGatewayOrderId());

        return PaymentResponse.from(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse handleCallback(PaymentCallbackRequest callback) {
        log.info("Handling payment callback for order: {}", callback.orderId());

        Payment payment = paymentRepository.findByGatewayOrderId(callback.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + callback.orderId()));

        if (payment.getStatus() == PaymentGatewayStatus.SUCCESS) {
            return PaymentResponse.from(payment);
        }

        if ("SUCCESS".equalsIgnoreCase(callback.status()) || "captured".equalsIgnoreCase(callback.status())) {
            // Verify signature (in production, use proper verification)
            payment.markSuccess(callback.paymentId(), callback.signature());

            // Confirm the booking
            PaymentConfirmation confirmation = new PaymentConfirmation(
                    payment.getAmount(),
                    callback.paymentId(),
                    payment.getPaymentMethod().name()
            );
            bookingService.confirmBooking(payment.getBooking().getId(), confirmation);

            log.info("Payment successful: {}", callback.paymentId());
        } else {
            payment.markFailed(callback.errorCode(), callback.errorDescription());
            log.warn("Payment failed: {} - {}", callback.errorCode(), callback.errorDescription());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentResponse.from(updatedPayment);
    }

    @Override
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        return PaymentResponse.from(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void initiateRefund(Long paymentId, String reason) {
        log.info("Initiating refund for payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentGatewayStatus.SUCCESS) {
            throw new BusinessException("Can only refund successful payments");
        }

        Refund refund = Refund.builder()
                .payment(payment)
                .amount(payment.getAmount())
                .reason(reason)
                .status(RefundStatus.PENDING)
                .build();

        refundRepository.save(refund);
        payment.setStatus(PaymentGatewayStatus.REFUND_INITIATED);
        paymentRepository.save(payment);

        log.info("Refund initiated for payment: {}", paymentId);
    }

    private String generateOrderId() {
        return "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
