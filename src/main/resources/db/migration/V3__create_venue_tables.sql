-- =====================================================
-- V3: Create Venue Tables
-- Venues include Theaters/Multiplexes
-- =====================================================

-- Cities
CREATE TABLE cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL DEFAULT 'India',
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,

    UNIQUE KEY uk_city_name_state (name, state),
    INDEX idx_cities_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Venues (Theaters/Multiplexes)
CREATE TABLE venues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    city_id BIGINT NOT NULL,
    address TEXT NOT NULL,
    landmark VARCHAR(200),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    contact_phone VARCHAR(15),
    contact_email VARCHAR(255),
    facilities JSON,  -- {"parking": true, "food_court": true, "wheelchair": true}
    status ENUM('ACTIVE', 'INACTIVE', 'UNDER_MAINTENANCE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (city_id) REFERENCES cities(id),
    INDEX idx_venues_city (city_id),
    INDEX idx_venues_status (status),
    FULLTEXT INDEX idx_venues_search (name, address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Screens
CREATE TABLE screens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    screen_type ENUM('REGULAR', 'IMAX', '4DX', 'DOLBY_ATMOS', 'PREMIUM', 'GOLD') NOT NULL DEFAULT 'REGULAR',
    total_seats INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
    UNIQUE KEY uk_screen_venue_name (venue_id, name),
    INDEX idx_screens_venue (venue_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seats (Seat Layout)
CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screen_id BIGINT NOT NULL,
    row_name VARCHAR(5) NOT NULL,
    seat_number INT NOT NULL,
    seat_label VARCHAR(10) NOT NULL,  -- e.g., "A1", "B12"
    seat_type ENUM('REGULAR', 'PREMIUM', 'RECLINER', 'VIP', 'WHEELCHAIR') NOT NULL DEFAULT 'REGULAR',
    x_position INT NOT NULL,  -- For UI rendering
    y_position INT NOT NULL,  -- For UI rendering
    is_available BOOLEAN DEFAULT TRUE,  -- For broken/maintenance seats

    FOREIGN KEY (screen_id) REFERENCES screens(id) ON DELETE CASCADE,
    UNIQUE KEY uk_seat_screen_row_num (screen_id, row_name, seat_number),
    INDEX idx_seats_screen (screen_id),
    INDEX idx_seats_type (seat_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
