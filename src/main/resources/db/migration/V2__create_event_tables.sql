-- =====================================================
-- V2: Create Event Tables
-- Events include Movies, Concerts, Sports, etc.
-- =====================================================

-- Events table (Movies, Concerts, Sports, etc.)
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category ENUM('MOVIE', 'CONCERT', 'SPORT', 'PLAY', 'COMEDY', 'OTHER') NOT NULL,
    language VARCHAR(20),
    duration_minutes INT,
    rating ENUM('U', 'UA', 'A', 'S') DEFAULT 'UA',
    genre VARCHAR(100),
    poster_url VARCHAR(500),
    banner_url VARCHAR(500),
    trailer_url VARCHAR(500),
    release_date DATE,
    status ENUM('COMING_SOON', 'NOW_SHOWING', 'ENDED') NOT NULL DEFAULT 'COMING_SOON',
    avg_rating DECIMAL(2,1) DEFAULT 0.0,
    total_reviews INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FULLTEXT INDEX idx_events_search (title, description, genre),
    INDEX idx_events_category (category),
    INDEX idx_events_status (status),
    INDEX idx_events_release_date (release_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event Cast
CREATE TABLE event_cast (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    person_name VARCHAR(100) NOT NULL,
    role_type ENUM('ACTOR', 'DIRECTOR', 'PRODUCER', 'MUSICIAN', 'OTHER') NOT NULL,
    character_name VARCHAR(100),
    image_url VARCHAR(500),
    display_order INT DEFAULT 0,

    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_cast_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
