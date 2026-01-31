-- =====================================================
-- V4: Create Show Tables
-- Shows represent showtimes for events at venues
-- =====================================================

-- Shows (Showtimes)
CREATE TABLE shows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    screen_id BIGINT NOT NULL,
    show_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'CANCELLED', 'COMPLETED', 'HOUSEFULL') NOT NULL DEFAULT 'SCHEDULED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (screen_id) REFERENCES screens(id),
    UNIQUE KEY uk_show_screen_datetime (screen_id, show_date, start_time),
    INDEX idx_shows_event (event_id),
    INDEX idx_shows_screen_date (screen_id, show_date),
    INDEX idx_shows_date (show_date),
    INDEX idx_shows_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Show Pricing (Different prices for different seat types)
CREATE TABLE show_pricing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    show_id BIGINT NOT NULL,
    seat_type ENUM('REGULAR', 'PREMIUM', 'RECLINER', 'VIP', 'WHEELCHAIR') NOT NULL,
    price DECIMAL(10, 2) NOT NULL,

    FOREIGN KEY (show_id) REFERENCES shows(id) ON DELETE CASCADE,
    UNIQUE KEY uk_pricing_show_seat (show_id, seat_type),
    INDEX idx_pricing_show (show_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
