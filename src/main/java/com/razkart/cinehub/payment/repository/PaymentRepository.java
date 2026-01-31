package com.razkart.cinehub.payment.repository;

import com.razkart.cinehub.payment.entity.Payment;
import com.razkart.cinehub.payment.entity.PaymentGatewayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);

    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByBookingIdAndStatus(Long bookingId, PaymentGatewayStatus status);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
