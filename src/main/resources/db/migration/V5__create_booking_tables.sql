-- =====================================================
-- V5: Create Booking Tables
-- CRITICAL: Core booking functionality
-- =====================================================

-- Bookings
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_number VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    convenience_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    final_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED', 'PARTIAL_REFUND') NOT NULL DEFAULT 'PENDING',
    booked_at DATETIME,
    expires_at DATETIME,
    cancelled_at DATETIME,
    cancellation_reason VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (show_id) REFERENCES shows(id),
    INDEX idx_bookings_user (user_id),
    INDEX idx_bookings_show (show_id),
    INDEX idx_bookings_status (status),
    INDEX idx_bookings_number (booking_number),
    INDEX idx_bookings_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Booked Seats (Junction table)
-- CRITICAL: UNIQUE KEY prevents double booking at database level!
CREATE TABLE booked_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    seat_label VARCHAR(10) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,

    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(id),
    FOREIGN KEY (seat_id) REFERENCES seats(id),
    UNIQUE KEY uk_booked_seat_show (show_id, seat_id),  -- CRITICAL: Prevents double booking!
    INDEX idx_booked_seats_booking (booking_id),
    INDEX idx_booked_seats_show (show_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tickets
CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    ticket_number VARCHAR(30) NOT NULL UNIQUE,
    seat_label VARCHAR(10) NOT NULL,
    qr_code TEXT NOT NULL,
    status ENUM('VALID', 'USED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'VALID',
    scanned_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    INDEX idx_tickets_booking (booking_id),
    INDEX idx_tickets_number (ticket_number),
    INDEX idx_tickets_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
