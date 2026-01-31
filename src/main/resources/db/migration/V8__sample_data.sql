-- =====================================================
-- V8: Sample Data for Development and Testing
-- Venues, Screens, Seats, Events, Cast, Shows
-- =====================================================

-- Insert sample venues (Mumbai cityId=1, Delhi cityId=2, Bangalore cityId=3)
INSERT INTO venues (name, address, city_id, latitude, longitude, contact_phone, contact_email, status) VALUES
('PVR Phoenix', 'Phoenix Marketcity, Kurla West, Mumbai', 1, 19.0863, 72.8894, '022-12345678', 'phoenix@pvr.com', 'ACTIVE'),
('INOX Metro', 'Central Railway Station, Mumbai', 1, 18.9698, 72.8191, '022-23456789', 'metro@inox.com', 'ACTIVE'),
('Cinepolis Andheri', 'Infiniti Mall, Andheri West, Mumbai', 1, 19.1364, 72.8296, '022-34567890', 'andheri@cinepolis.com', 'ACTIVE'),
('PVR Select City', 'Select Citywalk, Saket, Delhi', 2, 28.5289, 77.2191, '011-12345678', 'saket@pvr.com', 'ACTIVE'),
('INOX Connaught Place', 'Regal Building, CP, Delhi', 2, 28.6315, 77.2167, '011-23456789', 'cp@inox.com', 'ACTIVE'),
('PVR Orion', 'Orion Mall, Rajajinagar, Bangalore', 3, 13.0118, 77.5568, '080-12345678', 'orion@pvr.com', 'ACTIVE'),
('INOX Garuda', 'Garuda Mall, Magrath Road, Bangalore', 3, 12.9704, 77.6099, '080-23456789', 'garuda@inox.com', 'ACTIVE');

-- Insert screens for venues
INSERT INTO screens (venue_id, name, screen_type, total_seats) VALUES
-- PVR Phoenix (venueId=1)
(1, 'Screen 1 - IMAX', 'IMAX', 250),
(1, 'Screen 2 - Dolby', 'DOLBY_ATMOS', 180),
(1, 'Screen 3', 'REGULAR', 150),
(1, 'Screen 4', 'REGULAR', 150),
-- INOX Metro (venueId=2)
(2, 'Audi 1', 'DOLBY_ATMOS', 200),
(2, 'Audi 2', 'REGULAR', 150),
(2, 'Audi 3', 'REGULAR', 120),
-- Cinepolis Andheri (venueId=3)
(3, 'Screen 1 - 4DX', '4DX', 100),
(3, 'Screen 2 - IMAX', 'IMAX', 280),
(3, 'Screen 3', 'REGULAR', 180),
(3, 'Screen 4', 'REGULAR', 180),
(3, 'Screen 5', 'REGULAR', 150),
-- PVR Select City (venueId=4)
(4, 'Screen 1 - IMAX', 'IMAX', 300),
(4, 'Screen 2 - Gold', 'GOLD', 50),
(4, 'Screen 3 - Dolby', 'DOLBY_ATMOS', 200),
(4, 'Screen 4', 'REGULAR', 180),
(4, 'Screen 5', 'REGULAR', 180),
(4, 'Screen 6', 'REGULAR', 150),
-- INOX CP (venueId=5)
(5, 'Main Hall', 'DOLBY_ATMOS', 350),
(5, 'Hall 2', 'REGULAR', 200),
(5, 'Hall 3', 'REGULAR', 150),
-- PVR Orion (venueId=6)
(6, 'Screen 1 - IMAX', 'IMAX', 280),
(6, 'Screen 2 - Luxe', 'GOLD', 60),
(6, 'Screen 3', 'DOLBY_ATMOS', 200),
(6, 'Screen 4', 'REGULAR', 180),
(6, 'Screen 5', 'REGULAR', 150),
-- INOX Garuda (venueId=7)
(7, 'Audi 1 - 4DX', '4DX', 80),
(7, 'Audi 2', 'DOLBY_ATMOS', 200),
(7, 'Audi 3', 'REGULAR', 180),
(7, 'Audi 4', 'REGULAR', 150);

-- Insert seats for Screen 1 (IMAX - PVR Phoenix, screenId=1)
INSERT INTO seats (screen_id, row_name, seat_number, seat_type, seat_label, x_position, y_position, is_available) VALUES
-- Row A (Premium)
(1, 'A', 1, 'PREMIUM', 'A1', 1, 1, TRUE), (1, 'A', 2, 'PREMIUM', 'A2', 2, 1, TRUE), (1, 'A', 3, 'PREMIUM', 'A3', 3, 1, TRUE),
(1, 'A', 4, 'PREMIUM', 'A4', 4, 1, TRUE), (1, 'A', 5, 'PREMIUM', 'A5', 5, 1, TRUE), (1, 'A', 6, 'PREMIUM', 'A6', 6, 1, TRUE),
(1, 'A', 7, 'PREMIUM', 'A7', 7, 1, TRUE), (1, 'A', 8, 'PREMIUM', 'A8', 8, 1, TRUE), (1, 'A', 9, 'PREMIUM', 'A9', 9, 1, TRUE),
(1, 'A', 10, 'PREMIUM', 'A10', 10, 1, TRUE),
-- Row B (Premium)
(1, 'B', 1, 'PREMIUM', 'B1', 1, 2, TRUE), (1, 'B', 2, 'PREMIUM', 'B2', 2, 2, TRUE), (1, 'B', 3, 'PREMIUM', 'B3', 3, 2, TRUE),
(1, 'B', 4, 'PREMIUM', 'B4', 4, 2, TRUE), (1, 'B', 5, 'PREMIUM', 'B5', 5, 2, TRUE), (1, 'B', 6, 'PREMIUM', 'B6', 6, 2, TRUE),
(1, 'B', 7, 'PREMIUM', 'B7', 7, 2, TRUE), (1, 'B', 8, 'PREMIUM', 'B8', 8, 2, TRUE), (1, 'B', 9, 'PREMIUM', 'B9', 9, 2, TRUE),
(1, 'B', 10, 'PREMIUM', 'B10', 10, 2, TRUE),
-- Row C (Recliner)
(1, 'C', 1, 'RECLINER', 'C1', 1, 3, TRUE), (1, 'C', 2, 'RECLINER', 'C2', 2, 3, TRUE), (1, 'C', 3, 'RECLINER', 'C3', 3, 3, TRUE),
(1, 'C', 4, 'RECLINER', 'C4', 4, 3, TRUE), (1, 'C', 5, 'RECLINER', 'C5', 5, 3, TRUE), (1, 'C', 6, 'RECLINER', 'C6', 6, 3, TRUE),
(1, 'C', 7, 'RECLINER', 'C7', 7, 3, TRUE), (1, 'C', 8, 'RECLINER', 'C8', 8, 3, TRUE), (1, 'C', 9, 'RECLINER', 'C9', 9, 3, TRUE),
(1, 'C', 10, 'RECLINER', 'C10', 10, 3, TRUE), (1, 'C', 11, 'RECLINER', 'C11', 11, 3, TRUE), (1, 'C', 12, 'RECLINER', 'C12', 12, 3, TRUE),
-- Row D (Recliner)
(1, 'D', 1, 'RECLINER', 'D1', 1, 4, TRUE), (1, 'D', 2, 'RECLINER', 'D2', 2, 4, TRUE), (1, 'D', 3, 'RECLINER', 'D3', 3, 4, TRUE),
(1, 'D', 4, 'RECLINER', 'D4', 4, 4, TRUE), (1, 'D', 5, 'RECLINER', 'D5', 5, 4, TRUE), (1, 'D', 6, 'RECLINER', 'D6', 6, 4, TRUE),
(1, 'D', 7, 'RECLINER', 'D7', 7, 4, TRUE), (1, 'D', 8, 'RECLINER', 'D8', 8, 4, TRUE), (1, 'D', 9, 'RECLINER', 'D9', 9, 4, TRUE),
(1, 'D', 10, 'RECLINER', 'D10', 10, 4, TRUE), (1, 'D', 11, 'RECLINER', 'D11', 11, 4, TRUE), (1, 'D', 12, 'RECLINER', 'D12', 12, 4, TRUE),
-- Row E-F (Regular)
(1, 'E', 1, 'REGULAR', 'E1', 1, 5, TRUE), (1, 'E', 2, 'REGULAR', 'E2', 2, 5, TRUE), (1, 'E', 3, 'REGULAR', 'E3', 3, 5, TRUE),
(1, 'E', 4, 'REGULAR', 'E4', 4, 5, TRUE), (1, 'E', 5, 'REGULAR', 'E5', 5, 5, TRUE), (1, 'E', 6, 'REGULAR', 'E6', 6, 5, TRUE),
(1, 'E', 7, 'REGULAR', 'E7', 7, 5, TRUE), (1, 'E', 8, 'REGULAR', 'E8', 8, 5, TRUE), (1, 'E', 9, 'REGULAR', 'E9', 9, 5, TRUE),
(1, 'E', 10, 'REGULAR', 'E10', 10, 5, TRUE), (1, 'E', 11, 'REGULAR', 'E11', 11, 5, TRUE), (1, 'E', 12, 'REGULAR', 'E12', 12, 5, TRUE),
(1, 'E', 13, 'REGULAR', 'E13', 13, 5, TRUE), (1, 'E', 14, 'REGULAR', 'E14', 14, 5, TRUE),
(1, 'F', 1, 'REGULAR', 'F1', 1, 6, TRUE), (1, 'F', 2, 'REGULAR', 'F2', 2, 6, TRUE), (1, 'F', 3, 'REGULAR', 'F3', 3, 6, TRUE),
(1, 'F', 4, 'REGULAR', 'F4', 4, 6, TRUE), (1, 'F', 5, 'REGULAR', 'F5', 5, 6, TRUE), (1, 'F', 6, 'REGULAR', 'F6', 6, 6, TRUE),
(1, 'F', 7, 'REGULAR', 'F7', 7, 6, TRUE), (1, 'F', 8, 'REGULAR', 'F8', 8, 6, TRUE), (1, 'F', 9, 'REGULAR', 'F9', 9, 6, TRUE),
(1, 'F', 10, 'REGULAR', 'F10', 10, 6, TRUE), (1, 'F', 11, 'REGULAR', 'F11', 11, 6, TRUE), (1, 'F', 12, 'REGULAR', 'F12', 12, 6, TRUE),
(1, 'F', 13, 'REGULAR', 'F13', 13, 6, TRUE), (1, 'F', 14, 'REGULAR', 'F14', 14, 6, TRUE);

-- Insert seats for Screen 2 (screenId=2) - smaller screen
INSERT INTO seats (screen_id, row_name, seat_number, seat_type, seat_label, x_position, y_position, is_available) VALUES
(2, 'A', 1, 'PREMIUM', 'A1', 1, 1, TRUE), (2, 'A', 2, 'PREMIUM', 'A2', 2, 1, TRUE), (2, 'A', 3, 'PREMIUM', 'A3', 3, 1, TRUE),
(2, 'A', 4, 'PREMIUM', 'A4', 4, 1, TRUE), (2, 'A', 5, 'PREMIUM', 'A5', 5, 1, TRUE), (2, 'A', 6, 'PREMIUM', 'A6', 6, 1, TRUE),
(2, 'A', 7, 'PREMIUM', 'A7', 7, 1, TRUE), (2, 'A', 8, 'PREMIUM', 'A8', 8, 1, TRUE),
(2, 'B', 1, 'RECLINER', 'B1', 1, 2, TRUE), (2, 'B', 2, 'RECLINER', 'B2', 2, 2, TRUE), (2, 'B', 3, 'RECLINER', 'B3', 3, 2, TRUE),
(2, 'B', 4, 'RECLINER', 'B4', 4, 2, TRUE), (2, 'B', 5, 'RECLINER', 'B5', 5, 2, TRUE), (2, 'B', 6, 'RECLINER', 'B6', 6, 2, TRUE),
(2, 'B', 7, 'RECLINER', 'B7', 7, 2, TRUE), (2, 'B', 8, 'RECLINER', 'B8', 8, 2, TRUE), (2, 'B', 9, 'RECLINER', 'B9', 9, 2, TRUE),
(2, 'B', 10, 'RECLINER', 'B10', 10, 2, TRUE),
(2, 'C', 1, 'REGULAR', 'C1', 1, 3, TRUE), (2, 'C', 2, 'REGULAR', 'C2', 2, 3, TRUE), (2, 'C', 3, 'REGULAR', 'C3', 3, 3, TRUE),
(2, 'C', 4, 'REGULAR', 'C4', 4, 3, TRUE), (2, 'C', 5, 'REGULAR', 'C5', 5, 3, TRUE), (2, 'C', 6, 'REGULAR', 'C6', 6, 3, TRUE),
(2, 'C', 7, 'REGULAR', 'C7', 7, 3, TRUE), (2, 'C', 8, 'REGULAR', 'C8', 8, 3, TRUE), (2, 'C', 9, 'REGULAR', 'C9', 9, 3, TRUE),
(2, 'C', 10, 'REGULAR', 'C10', 10, 3, TRUE), (2, 'C', 11, 'REGULAR', 'C11', 11, 3, TRUE), (2, 'C', 12, 'REGULAR', 'C12', 12, 3, TRUE),
(2, 'D', 1, 'REGULAR', 'D1', 1, 4, TRUE), (2, 'D', 2, 'REGULAR', 'D2', 2, 4, TRUE), (2, 'D', 3, 'REGULAR', 'D3', 3, 4, TRUE),
(2, 'D', 4, 'REGULAR', 'D4', 4, 4, TRUE), (2, 'D', 5, 'REGULAR', 'D5', 5, 4, TRUE), (2, 'D', 6, 'REGULAR', 'D6', 6, 4, TRUE),
(2, 'D', 7, 'REGULAR', 'D7', 7, 4, TRUE), (2, 'D', 8, 'REGULAR', 'D8', 8, 4, TRUE), (2, 'D', 9, 'REGULAR', 'D9', 9, 4, TRUE),
(2, 'D', 10, 'REGULAR', 'D10', 10, 4, TRUE), (2, 'D', 11, 'REGULAR', 'D11', 11, 4, TRUE), (2, 'D', 12, 'REGULAR', 'D12', 12, 4, TRUE);

-- Insert sample events (movies)
INSERT INTO events (title, description, category, language, duration_minutes, rating, genre, release_date, poster_url, trailer_url, status) VALUES
('Pushpa 2: The Rule', 'Pushpa Raj returns to continue his reign as the ultimate kingpin. This action-packed sequel follows his rise and battles against adversaries.', 'MOVIE', 'Telugu', 180, 'UA', 'Action, Drama', '2024-12-05', '/posters/pushpa2.jpg', 'https://youtube.com/watch?v=pushpa2', 'NOW_SHOWING'),
('Stree 2', 'The horror comedy returns as the team faces a new supernatural threat terrorizing their town.', 'MOVIE', 'Hindi', 150, 'UA', 'Horror, Comedy', '2024-08-15', '/posters/stree2.jpg', 'https://youtube.com/watch?v=stree2', 'NOW_SHOWING'),
('Kalki 2898 AD', 'A sci-fi epic set in a dystopian future where mythology meets technology.', 'MOVIE', 'Telugu', 180, 'UA', 'Sci-Fi, Action', '2024-06-27', '/posters/kalki.jpg', 'https://youtube.com/watch?v=kalki', 'NOW_SHOWING'),
('Fighter', 'Indian Air Force pilots defend the nation against external threats in this aerial action drama.', 'MOVIE', 'Hindi', 165, 'UA', 'Action, Drama', '2024-01-25', '/posters/fighter.jpg', 'https://youtube.com/watch?v=fighter', 'NOW_SHOWING'),
('Devara', 'A powerful story of a man who rules the seas and the challenges he faces.', 'MOVIE', 'Telugu', 175, 'UA', 'Action, Thriller', '2024-09-27', '/posters/devara.jpg', 'https://youtube.com/watch?v=devara', 'NOW_SHOWING'),
('Singham Again', 'Bajirao Singham returns for another action-packed adventure fighting crime and corruption.', 'MOVIE', 'Hindi', 160, 'UA', 'Action', '2024-11-01', '/posters/singham.jpg', 'https://youtube.com/watch?v=singham', 'COMING_SOON'),
('Bhool Bhulaiyaa 3', 'The horror comedy franchise continues with more scares and laughs.', 'MOVIE', 'Hindi', 145, 'UA', 'Horror, Comedy', '2024-11-01', '/posters/bb3.jpg', 'https://youtube.com/watch?v=bb3', 'COMING_SOON'),
('Amaran', 'A biographical war film based on the life of Major Mukund Varadarajan.', 'MOVIE', 'Tamil', 155, 'UA', 'Biography, War', '2024-10-31', '/posters/amaran.jpg', 'https://youtube.com/watch?v=amaran', 'NOW_SHOWING');

-- Insert cast members
INSERT INTO event_cast (event_id, person_name, role_type, character_name, image_url, display_order) VALUES
-- Pushpa 2 cast (eventId=1)
(1, 'Allu Arjun', 'ACTOR', 'Pushpa Raj', '/cast/alluarjun.jpg', 1),
(1, 'Rashmika Mandanna', 'ACTOR', 'Srivalli', '/cast/rashmika.jpg', 2),
(1, 'Fahadh Faasil', 'ACTOR', 'Bhanwar Singh Shekhawat', '/cast/fahadh.jpg', 3),
(1, 'Sukumar', 'DIRECTOR', NULL, '/cast/sukumar.jpg', 4),
-- Stree 2 cast (eventId=2)
(2, 'Rajkummar Rao', 'ACTOR', 'Vicky', '/cast/rajkummar.jpg', 1),
(2, 'Shraddha Kapoor', 'ACTOR', 'Stree', '/cast/shraddha.jpg', 2),
(2, 'Pankaj Tripathi', 'ACTOR', 'Rudra', '/cast/pankaj.jpg', 3),
(2, 'Amar Kaushik', 'DIRECTOR', NULL, '/cast/amar.jpg', 4),
-- Kalki cast (eventId=3)
(3, 'Prabhas', 'ACTOR', 'Bhairava', '/cast/prabhas.jpg', 1),
(3, 'Deepika Padukone', 'ACTOR', 'Sumathi', '/cast/deepika.jpg', 2),
(3, 'Amitabh Bachchan', 'ACTOR', 'Ashwatthama', '/cast/amitabh.jpg', 3),
(3, 'Nag Ashwin', 'DIRECTOR', NULL, '/cast/nagashwin.jpg', 4),
-- Fighter cast (eventId=4)
(4, 'Hrithik Roshan', 'ACTOR', 'Shamsher Pathania', '/cast/hrithik.jpg', 1),
(4, 'Deepika Padukone', 'ACTOR', 'Minal Rathore', '/cast/deepika.jpg', 2),
(4, 'Anil Kapoor', 'ACTOR', 'Rocky', '/cast/anil.jpg', 3),
(4, 'Siddharth Anand', 'DIRECTOR', NULL, '/cast/siddharth.jpg', 4),
-- Devara cast (eventId=5)
(5, 'Jr NTR', 'ACTOR', 'Devara', '/cast/ntr.jpg', 1),
(5, 'Janhvi Kapoor', 'ACTOR', 'Thangam', '/cast/janhvi.jpg', 2),
(5, 'Saif Ali Khan', 'ACTOR', 'Bhaira', '/cast/saif.jpg', 3),
(5, 'Koratala Siva', 'DIRECTOR', NULL, '/cast/koratala.jpg', 4);

-- Insert shows for the next 7 days
-- Shows for Pushpa 2 at PVR Phoenix Screen 1 (IMAX)
INSERT INTO shows (event_id, screen_id, show_date, start_time, end_time, status) VALUES
(1, 1, CURDATE(), '09:30:00', '12:30:00', 'SCHEDULED'),
(1, 1, CURDATE(), '13:00:00', '16:00:00', 'SCHEDULED'),
(1, 1, CURDATE(), '16:30:00', '19:30:00', 'SCHEDULED'),
(1, 1, CURDATE(), '20:00:00', '23:00:00', 'SCHEDULED'),
(1, 1, CURDATE() + INTERVAL 1 DAY, '09:30:00', '12:30:00', 'SCHEDULED'),
(1, 1, CURDATE() + INTERVAL 1 DAY, '13:00:00', '16:00:00', 'SCHEDULED'),
(1, 1, CURDATE() + INTERVAL 1 DAY, '16:30:00', '19:30:00', 'SCHEDULED'),
(1, 1, CURDATE() + INTERVAL 1 DAY, '20:00:00', '23:00:00', 'SCHEDULED');

-- Shows for Stree 2 at PVR Phoenix Screen 2 (Dolby)
INSERT INTO shows (event_id, screen_id, show_date, start_time, end_time, status) VALUES
(2, 2, CURDATE(), '10:00:00', '12:30:00', 'SCHEDULED'),
(2, 2, CURDATE(), '14:00:00', '16:30:00', 'SCHEDULED'),
(2, 2, CURDATE(), '18:00:00', '20:30:00', 'SCHEDULED'),
(2, 2, CURDATE(), '21:00:00', '23:30:00', 'SCHEDULED'),
(2, 2, CURDATE() + INTERVAL 1 DAY, '10:00:00', '12:30:00', 'SCHEDULED'),
(2, 2, CURDATE() + INTERVAL 1 DAY, '14:00:00', '16:30:00', 'SCHEDULED');

-- Shows for Kalki at INOX Metro Screen 1 (Dolby) - screenId=5
INSERT INTO shows (event_id, screen_id, show_date, start_time, end_time, status) VALUES
(3, 5, CURDATE(), '09:00:00', '12:00:00', 'SCHEDULED'),
(3, 5, CURDATE(), '13:00:00', '16:00:00', 'SCHEDULED'),
(3, 5, CURDATE(), '17:00:00', '20:00:00', 'SCHEDULED'),
(3, 5, CURDATE(), '21:00:00', '00:00:00', 'SCHEDULED');

-- Shows for Fighter at PVR Select City IMAX (Delhi) - screenId=13
INSERT INTO shows (event_id, screen_id, show_date, start_time, end_time, status) VALUES
(4, 13, CURDATE(), '10:00:00', '12:45:00', 'SCHEDULED'),
(4, 13, CURDATE(), '14:00:00', '16:45:00', 'SCHEDULED'),
(4, 13, CURDATE(), '18:00:00', '20:45:00', 'SCHEDULED'),
(4, 13, CURDATE() + INTERVAL 1 DAY, '10:00:00', '12:45:00', 'SCHEDULED'),
(4, 13, CURDATE() + INTERVAL 1 DAY, '14:00:00', '16:45:00', 'SCHEDULED');

-- Shows for Devara at PVR Orion IMAX (Bangalore) - screenId=22
INSERT INTO shows (event_id, screen_id, show_date, start_time, end_time, status) VALUES
(5, 22, CURDATE(), '09:30:00', '12:25:00', 'SCHEDULED'),
(5, 22, CURDATE(), '13:30:00', '16:25:00', 'SCHEDULED'),
(5, 22, CURDATE(), '17:30:00', '20:25:00', 'SCHEDULED'),
(5, 22, CURDATE(), '21:00:00', '23:55:00', 'SCHEDULED');

-- Insert show pricing (linked to shows)
-- Pricing for Pushpa 2 shows at Screen 1 (showIds 1-8)
INSERT INTO show_pricing (show_id, seat_type, price) VALUES
(1, 'PREMIUM', 550.00), (1, 'RECLINER', 450.00), (1, 'REGULAR', 350.00),
(2, 'PREMIUM', 550.00), (2, 'RECLINER', 450.00), (2, 'REGULAR', 350.00),
(3, 'PREMIUM', 550.00), (3, 'RECLINER', 450.00), (3, 'REGULAR', 350.00),
(4, 'PREMIUM', 600.00), (4, 'RECLINER', 500.00), (4, 'REGULAR', 400.00),
(5, 'PREMIUM', 500.00), (5, 'RECLINER', 400.00), (5, 'REGULAR', 300.00),
(6, 'PREMIUM', 550.00), (6, 'RECLINER', 450.00), (6, 'REGULAR', 350.00),
(7, 'PREMIUM', 550.00), (7, 'RECLINER', 450.00), (7, 'REGULAR', 350.00),
(8, 'PREMIUM', 600.00), (8, 'RECLINER', 500.00), (8, 'REGULAR', 400.00);

-- Pricing for Stree 2 shows at Screen 2 (showIds 9-14)
INSERT INTO show_pricing (show_id, seat_type, price) VALUES
(9, 'PREMIUM', 450.00), (9, 'RECLINER', 350.00), (9, 'REGULAR', 250.00),
(10, 'PREMIUM', 450.00), (10, 'RECLINER', 350.00), (10, 'REGULAR', 250.00),
(11, 'PREMIUM', 500.00), (11, 'RECLINER', 400.00), (11, 'REGULAR', 300.00),
(12, 'PREMIUM', 550.00), (12, 'RECLINER', 450.00), (12, 'REGULAR', 350.00),
(13, 'PREMIUM', 400.00), (13, 'RECLINER', 300.00), (13, 'REGULAR', 200.00),
(14, 'PREMIUM', 450.00), (14, 'RECLINER', 350.00), (14, 'REGULAR', 250.00);

-- Pricing for Kalki shows (showIds 15-18)
INSERT INTO show_pricing (show_id, seat_type, price) VALUES
(15, 'PREMIUM', 500.00), (15, 'RECLINER', 400.00), (15, 'REGULAR', 300.00),
(16, 'PREMIUM', 550.00), (16, 'RECLINER', 450.00), (16, 'REGULAR', 350.00),
(17, 'PREMIUM', 600.00), (17, 'RECLINER', 500.00), (17, 'REGULAR', 400.00),
(18, 'PREMIUM', 650.00), (18, 'RECLINER', 550.00), (18, 'REGULAR', 450.00);

-- Pricing for Fighter shows (showIds 19-23)
INSERT INTO show_pricing (show_id, seat_type, price) VALUES
(19, 'PREMIUM', 600.00), (19, 'RECLINER', 500.00), (19, 'REGULAR', 400.00),
(20, 'PREMIUM', 650.00), (20, 'RECLINER', 550.00), (20, 'REGULAR', 450.00),
(21, 'PREMIUM', 700.00), (21, 'RECLINER', 600.00), (21, 'REGULAR', 500.00),
(22, 'PREMIUM', 550.00), (22, 'RECLINER', 450.00), (22, 'REGULAR', 350.00),
(23, 'PREMIUM', 600.00), (23, 'RECLINER', 500.00), (23, 'REGULAR', 400.00);

-- Pricing for Devara shows (showIds 24-27)
INSERT INTO show_pricing (show_id, seat_type, price) VALUES
(24, 'PREMIUM', 550.00), (24, 'RECLINER', 450.00), (24, 'REGULAR', 350.00),
(25, 'PREMIUM', 600.00), (25, 'RECLINER', 500.00), (25, 'REGULAR', 400.00),
(26, 'PREMIUM', 650.00), (26, 'RECLINER', 550.00), (26, 'REGULAR', 450.00),
(27, 'PREMIUM', 700.00), (27, 'RECLINER', 600.00), (27, 'REGULAR', 500.00);

-- Insert a test customer user (password: test123)
INSERT INTO users (email, phone, password_hash, full_name, role, status, email_verified)
VALUES ('testuser@cinehub.com', '9876543210',
        '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqBuBifTm0EhPmCqcKQqH5bFvHKHO',
        'Test User', 'CUSTOMER', 'ACTIVE', TRUE);
