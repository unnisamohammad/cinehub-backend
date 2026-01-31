-- =====================================================
-- V6: Create Payment Tables
-- Payment processing and refund management
-- =====================================================

-- Payments
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('UPI', 'CREDIT_CARD', 'DEBIT_CARD', 'NETBANKING', 'WALLET') NOT NULL,
    payment_gateway VARCHAR(30) NOT NULL,  -- RAZORPAY, PAYTM, STRIPE
    gateway_order_id VARCHAR(100),
    gateway_payment_id VARCHAR(100),
    gateway_signature VARCHAR(255),
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    status ENUM('INITIATED', 'PROCESSING', 'SUCCESS', 'FAILED', 'REFUND_INITIATED', 'REFUNDED') NOT NULL DEFAULT 'INITIATED',
    failure_code VARCHAR(50),
    failure_reason TEXT,
    initiated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME,
    metadata JSON,

    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    INDEX idx_payments_booking (booking_id),
    INDEX idx_payments_gateway_order (gateway_order_id),
    INDEX idx_payments_idempotency (idempotency_key),
    INDEX idx_payments_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Refunds
CREATE TABLE refunds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    reason VARCHAR(500),
    gateway_refund_id VARCHAR(100),
    status ENUM('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'PENDING',
    processed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (payment_id) REFERENCES payments(id),
    INDEX idx_refunds_payment (payment_id),
    INDEX idx_refunds_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
