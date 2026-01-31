-- =====================================================
-- CineHub Database Schema for MySQL
-- Run this script to create all tables
-- =====================================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS cinehub;
USE cinehub;

-- =====================================================
-- USER MANAGEMENT TABLES
-- =====================================================

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    phone VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    version BIGINT DEFAULT 0,
    INDEX idx_users_email (email),
    INDEX idx_users_active (active)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_type VARCHAR(20) DEFAULT 'HOME',
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_addresses_user (user_id)
);

-- =====================================================
-- MOVIE MANAGEMENT TABLES
-- =====================================================

CREATE TABLE genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE languages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    release_date DATE,
    poster_url VARCHAR(500),
    trailer_url VARCHAR(500),
    rating DECIMAL(3,1) DEFAULT 0.0,
    certificate VARCHAR(10),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    version BIGINT DEFAULT 0,
    INDEX idx_movies_title (title),
    INDEX idx_movies_release (release_date),
    INDEX idx_movies_active (active)
);

CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE movie_languages (
    movie_id BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, language_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages(id) ON DELETE CASCADE
);

CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_review_user_movie (user_id, movie_id),
    INDEX idx_reviews_movie (movie_id)
);

-- =====================================================
-- THEATER MANAGEMENT TABLES
-- =====================================================

CREATE TABLE cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_city_state (name, state),
    INDEX idx_cities_active (active)
);

CREATE TABLE theaters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city_id BIGINT NOT NULL,
    owner_id BIGINT,
    phone VARCHAR(20),
    email VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (city_id) REFERENCES cities(id),
    FOREIGN KEY (owner_id) REFERENCES users(id),
    INDEX idx_theaters_city (city_id),
    INDEX idx_theaters_owner (owner_id),
    INDEX idx_theaters_active (active)
);

CREATE TABLE screens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    theater_id BIGINT NOT NULL,
    screen_type VARCHAR(20) DEFAULT 'REGULAR',
    total_rows INT NOT NULL,
    seats_per_row INT NOT NULL,
    total_seats INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (theater_id) REFERENCES theaters(id) ON DELETE CASCADE,
    UNIQUE KEY uk_screen_theater (theater_id, name),
    INDEX idx_screens_theater (theater_id)
);

CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screen_id BIGINT NOT NULL,
    row_name VARCHAR(5) NOT NULL,
    seat_number INT NOT NULL,
    seat_type VARCHAR(20) DEFAULT 'REGULAR',
    base_price DECIMAL(10,2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (screen_id) REFERENCES screens(id) ON DELETE CASCADE,
    UNIQUE KEY uk_seat_screen (screen_id, row_name, seat_number),
    INDEX idx_seats_screen (screen_id)
);

-- =====================================================
-- SHOW MANAGEMENT TABLES
-- =====================================================

CREATE TABLE shows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    screen_id BIGINT NOT NULL,
    show_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    price_multiplier DECIMAL(3,2) DEFAULT 1.00,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (screen_id) REFERENCES screens(id),
    INDEX idx_shows_movie_date (movie_id, show_date),
    INDEX idx_shows_screen_date (screen_id, show_date),
    INDEX idx_shows_date (show_date)
);

-- =====================================================
-- BOOKING MANAGEMENT TABLES
-- =====================================================

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_seats INT NOT NULL,
    base_amount DECIMAL(10,2) NOT NULL,
    convenience_fee DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(10,2) NOT NULL,
    booked_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    cancellation_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (show_id) REFERENCES shows(id),
    INDEX idx_bookings_user (user_id),
    INDEX idx_bookings_show (show_id),
    INDEX idx_bookings_reference (booking_reference),
    INDEX idx_bookings_status (status)
);

CREATE TABLE booked_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'LOCKED',
    lock_expires_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(id),
    FOREIGN KEY (show_id) REFERENCES shows(id),
    UNIQUE KEY uk_booked_seat_show (show_id, seat_id, status),
    INDEX idx_booked_seats_booking (booking_id),
    INDEX idx_booked_seats_show_status (show_id, status),
    INDEX idx_booked_seats_lock (status, lock_expires_at)
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_gateway VARCHAR(50),
    gateway_response TEXT,
    paid_at TIMESTAMP NULL,
    refund_amount DECIMAL(10,2) DEFAULT 0.00,
    refunded_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    INDEX idx_payments_booking (booking_id),
    INDEX idx_payments_status (status),
    INDEX idx_payments_txn (transaction_id)
);

-- =====================================================
-- SEED DATA
-- =====================================================

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('ROLE_GUEST', 'Guest user with read-only access'),
('ROLE_CUSTOMER', 'Registered customer who can book tickets'),
('ROLE_THEATER_ADMIN', 'Theater owner/admin who manages their theater'),
('ROLE_SUPER_ADMIN', 'Super admin with full system access');

-- Insert common genres
INSERT INTO genres (name) VALUES
('Action'), ('Adventure'), ('Animation'), ('Comedy'), ('Crime'),
('Documentary'), ('Drama'), ('Family'), ('Fantasy'), ('Horror'),
('Mystery'), ('Romance'), ('Sci-Fi'), ('Thriller'), ('War');

-- Insert common languages
INSERT INTO languages (name, code) VALUES
('English', 'en'), ('Hindi', 'hi'), ('Tamil', 'ta'), ('Telugu', 'te'),
('Malayalam', 'ml'), ('Kannada', 'kn'), ('Bengali', 'bn'), ('Marathi', 'mr');

-- Insert major Indian cities
INSERT INTO cities (name, state) VALUES
('Mumbai', 'Maharashtra'),
('Delhi', 'Delhi'),
('Bangalore', 'Karnataka'),
('Hyderabad', 'Telangana'),
('Chennai', 'Tamil Nadu'),
('Kolkata', 'West Bengal'),
('Pune', 'Maharashtra'),
('Ahmedabad', 'Gujarat'),
('Jaipur', 'Rajasthan'),
('Lucknow', 'Uttar Pradesh');
