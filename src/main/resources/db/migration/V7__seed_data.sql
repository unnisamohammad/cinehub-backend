-- =====================================================
-- V7: Seed Data
-- Initial data for cities and test users
-- =====================================================

-- Insert major Indian cities
INSERT INTO cities (name, state, country, is_active, display_order) VALUES
('Mumbai', 'Maharashtra', 'India', TRUE, 1),
('Delhi', 'Delhi', 'India', TRUE, 2),
('Bangalore', 'Karnataka', 'India', TRUE, 3),
('Hyderabad', 'Telangana', 'India', TRUE, 4),
('Chennai', 'Tamil Nadu', 'India', TRUE, 5),
('Kolkata', 'West Bengal', 'India', TRUE, 6),
('Pune', 'Maharashtra', 'India', TRUE, 7),
('Ahmedabad', 'Gujarat', 'India', TRUE, 8),
('Jaipur', 'Rajasthan', 'India', TRUE, 9),
('Lucknow', 'Uttar Pradesh', 'India', TRUE, 10);

-- Insert a test admin user (password: admin123 - BCrypt encoded)
-- In production, create users through the registration API
INSERT INTO users (email, phone, password_hash, full_name, role, status, email_verified)
VALUES ('admin@cinehub.com', '9999999999',
        '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqBuBifTm0EhPmCqcKQqH5bFvHKHO',
        'System Admin', 'ADMIN', 'ACTIVE', TRUE);
