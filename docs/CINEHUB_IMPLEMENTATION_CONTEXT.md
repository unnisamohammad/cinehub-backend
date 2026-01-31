# CINEHUB - Movie Ticketing Platform Implementation Guide
# For use with Claude Code in VS Code

## PROJECT CONTEXT

**Application Name:** CineHub
**Package:** com.razkart.cinehub
**Framework:** Spring Boot 4.0.2
**Java Version:** 21
**Database:** MySQL 8.0+
**Architecture:** Modular Monolith with Clean Architecture

---

## 1. PROJECT STRUCTURE

```
cinehub/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/razkart/cinehub/
│   │   │   ├── CinehubApplication.java
│   │   │   │
│   │   │   ├── common/                          # Shared utilities
│   │   │   │   ├── config/
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── RedisConfig.java
│   │   │   │   │   ├── RabbitMQConfig.java
│   │   │   │   │   └── OpenApiConfig.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   ├── BusinessException.java
│   │   │   │   │   ├── SeatNotAvailableException.java
│   │   │   │   │   └── PaymentFailedException.java
│   │   │   │   ├── dto/
│   │   │   │   │   └── ApiResponse.java
│   │   │   │   ├── util/
│   │   │   │   │   ├── JwtUtil.java
│   │   │   │   │   └── QRCodeGenerator.java
│   │   │   │   └── audit/
│   │   │   │       └── AuditingConfig.java
│   │   │   │
│   │   │   ├── user/                            # User Module
│   │   │   │   ├── entity/
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── UserRole.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── UserRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   └── UserServiceImpl.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── UserController.java
│   │   │   │   └── dto/
│   │   │   │       ├── RegisterRequest.java
│   │   │   │       ├── LoginRequest.java
│   │   │   │       ├── LoginResponse.java
│   │   │   │       └── UserResponse.java
│   │   │   │
│   │   │   ├── event/                           # Event/Movie Module
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Event.java
│   │   │   │   │   ├── EventCategory.java
│   │   │   │   │   ├── EventStatus.java
│   │   │   │   │   ├── Rating.java
│   │   │   │   │   └── Cast.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── EventRepository.java
│   │   │   │   │   └── CastRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── EventService.java
│   │   │   │   │   └── EventServiceImpl.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── EventController.java
│   │   │   │   └── dto/
│   │   │   │       ├── EventRequest.java
│   │   │   │       ├── EventResponse.java
│   │   │   │       └── EventSearchCriteria.java
│   │   │   │
│   │   │   ├── venue/                           # Venue Module
│   │   │   │   ├── entity/
│   │   │   │   │   ├── City.java
│   │   │   │   │   ├── Venue.java
│   │   │   │   │   ├── Screen.java
│   │   │   │   │   ├── ScreenType.java
│   │   │   │   │   ├── Seat.java
│   │   │   │   │   └── SeatType.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── CityRepository.java
│   │   │   │   │   ├── VenueRepository.java
│   │   │   │   │   ├── ScreenRepository.java
│   │   │   │   │   └── SeatRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── VenueService.java
│   │   │   │   │   └── VenueServiceImpl.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── VenueController.java
│   │   │   │   └── dto/
│   │   │   │       ├── VenueRequest.java
│   │   │   │       ├── VenueResponse.java
│   │   │   │       ├── ScreenResponse.java
│   │   │   │       └── SeatLayoutResponse.java
│   │   │   │
│   │   │   ├── show/                            # Show Module
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Show.java
│   │   │   │   │   ├── ShowStatus.java
│   │   │   │   │   └── ShowPricing.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── ShowRepository.java
│   │   │   │   │   └── ShowPricingRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── ShowService.java
│   │   │   │   │   └── ShowServiceImpl.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── ShowController.java
│   │   │   │   └── dto/
│   │   │   │       ├── ShowRequest.java
│   │   │   │       ├── ShowResponse.java
│   │   │   │       └── SeatAvailabilityResponse.java
│   │   │   │
│   │   │   ├── booking/                         # Booking Module (CRITICAL)
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Booking.java
│   │   │   │   │   ├── BookingStatus.java
│   │   │   │   │   ├── PaymentStatus.java
│   │   │   │   │   ├── BookedSeat.java
│   │   │   │   │   └── Ticket.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── BookingRepository.java
│   │   │   │   │   ├── BookedSeatRepository.java
│   │   │   │   │   └── TicketRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── BookingService.java
│   │   │   │   │   ├── BookingServiceImpl.java
│   │   │   │   │   ├── SeatLockService.java
│   │   │   │   │   └── SeatLockServiceImpl.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── BookingController.java
│   │   │   │   └── dto/
│   │   │   │       ├── BookingRequest.java
│   │   │   │       ├── BookingResponse.java
│   │   │   │       ├── TicketResponse.java
│   │   │   │       └── PricingDetail.java
│   │   │   │
│   │   │   ├── payment/                         # Payment Module
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Payment.java
│   │   │   │   │   ├── PaymentMethod.java
│   │   │   │   │   └── Refund.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── PaymentRepository.java
│   │   │   │   │   └── RefundRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── PaymentService.java
│   │   │   │   │   ├── PaymentServiceImpl.java
│   │   │   │   │   └── PaymentGatewayClient.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── PaymentController.java
│   │   │   │   └── dto/
│   │   │   │       ├── PaymentRequest.java
│   │   │   │       ├── PaymentResponse.java
│   │   │   │       └── PaymentCallbackRequest.java
│   │   │   │
│   │   │   ├── search/                          # Search Module
│   │   │   │   ├── service/
│   │   │   │   │   ├── SearchService.java
│   │   │   │   │   └── SearchServiceImpl.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── SearchController.java
│   │   │   │   └── dto/
│   │   │   │       └── SearchRequest.java
│   │   │   │
│   │   │   └── notification/                    # Notification Module
│   │   │       ├── service/
│   │   │       │   ├── NotificationService.java
│   │   │       │   ├── EmailService.java
│   │   │       │   └── SmsService.java
│   │   │       ├── listener/
│   │   │       │   └── BookingEventListener.java
│   │   │       └── dto/
│   │   │           └── NotificationPayload.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/                    # Flyway migrations
│   │           ├── V1__create_user_tables.sql
│   │           ├── V2__create_event_tables.sql
│   │           ├── V3__create_venue_tables.sql
│   │           ├── V4__create_show_tables.sql
│   │           ├── V5__create_booking_tables.sql
│   │           └── V6__create_payment_tables.sql
│   │
│   └── test/
│       └── java/com/razkart/cinehub/
│           ├── booking/
│           │   └── BookingServiceTest.java
│           └── integration/
│               └── BookingIntegrationTest.java
│
└── docker-compose.yml
```

---

## 2. MAVEN DEPENDENCIES (pom.xml additions)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
    
    <!-- MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Flyway for DB migrations -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-mysql</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- MapStruct for DTO mapping -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- OpenAPI/Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>
    
    <!-- QR Code Generation -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>3.5.3</version>
    </dependency>
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>javase</artifactId>
        <version>3.5.3</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.5.5.Final</version>
                    </path>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 3. APPLICATION CONFIGURATION

### application.yml

```yaml
spring:
  application:
    name: cinehub

  profiles:
    active: dev

  datasource:
    url: jdbc:mysql://localhost:3306/cinehub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
        default_batch_fetch_size: 20
    open-in-view: false

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 10
          max-idle: 5
          min-idle: 2

  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:guest}
    password: ${RABBITMQ_PASS:guest}

  jackson:
    serialization:
      write-dates-as-timestamps: false
    default-property-inclusion: non_null
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Kolkata

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    com.razkart.cinehub: DEBUG
    org.hibernate.SQL: DEBUG
    org.springframework.security: DEBUG

# Custom Application Properties
cinehub:
  jwt:
    secret: ${JWT_SECRET:your-256-bit-secret-key-here-make-it-long-enough}
    expiration: 86400000  # 24 hours in milliseconds
  booking:
    expiry-minutes: 10
    max-seats-per-booking: 10
    convenience-fee-percent: 5.0
    tax-percent: 18.0
  notification:
    email:
      from: noreply@cinehub.com
    sms:
      enabled: false

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

---

## 4. DATABASE SCHEMA (Flyway Migrations)

### V1__create_user_tables.sql

```sql
-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(15) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    profile_image VARCHAR(500),
    role ENUM('CUSTOMER', 'ADMIN', 'THEATER_OWNER') NOT NULL DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    
    INDEX idx_users_email (email),
    INDEX idx_users_phone (phone),
    INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### V2__create_event_tables.sql

```sql
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
```

### V3__create_venue_tables.sql

```sql
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
```

### V4__create_show_tables.sql

```sql
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
```

### V5__create_booking_tables.sql

```sql
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
```

### V6__create_payment_tables.sql

```sql
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
```

---

## 5. CORE ENTITY IMPLEMENTATIONS

### Base Entity (All entities extend this)

```java
package com.razkart.cinehub.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

### User Entity

```java
package com.razkart.cinehub.user.entity;

import com.razkart.cinehub.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true, length = 15)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.CUSTOMER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    @Builder.Default
    private Boolean phoneVerified = false;

    @Version
    private Integer version;

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
```

### Booking Entity (Most Critical)

```java
package com.razkart.cinehub.booking.entity;

import com.razkart.cinehub.common.entity.BaseEntity;
import com.razkart.cinehub.show.entity.Show;
import com.razkart.cinehub.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

    @Column(name = "booking_number", nullable = false, unique = true, length = 20)
    private String bookingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "convenience_fee", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal convenienceFee = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookedSeat> bookedSeats = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @Version
    private Integer version;

    // Business methods
    public void addSeat(BookedSeat seat) {
        bookedSeats.add(seat);
        seat.setBooking(this);
    }

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.bookedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public int getSeatCount() {
        return bookedSeats.size();
    }

    @PrePersist
    private void generateBookingNumber() {
        if (this.bookingNumber == null) {
            this.bookingNumber = "CH" + System.currentTimeMillis() +
                    String.format("%04d", (int) (Math.random() * 10000));
        }
    }
}
```

---

## 6. REDIS SEAT LOCKING SERVICE

```java
package com.razkart.cinehub.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockServiceImpl implements SeatLockService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String SEAT_LOCK_PREFIX = "cinehub:seat:lock:";
    private static final String USER_SEATS_PREFIX = "cinehub:user:seats:";

    @Override
    public List<Long> lockSeats(Long showId, List<Long> seatIds, Long userId, int expiryMinutes) {
        List<Long> lockedSeats = new ArrayList<>();
        Duration expiry = Duration.ofMinutes(expiryMinutes);

        for (Long seatId : seatIds) {
            String lockKey = buildSeatLockKey(showId, seatId);

            // SETNX - atomic operation, only succeeds if key doesn't exist
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, userId.toString(), expiry);

            if (Boolean.TRUE.equals(locked)) {
                lockedSeats.add(seatId);
                log.debug("Locked seat {} for user {} in show {}", seatId, userId, showId);
            } else {
                log.debug("Seat {} already locked in show {}", seatId, showId);
            }
        }

        // Track user's locked seats
        if (!lockedSeats.isEmpty()) {
            String userSeatsKey = buildUserSeatsKey(showId, userId);
            redisTemplate.opsForSet().add(userSeatsKey,
                    lockedSeats.stream().map(String::valueOf).toArray(String[]::new));
            redisTemplate.expire(userSeatsKey, expiry);
        }

        return lockedSeats;
    }

    @Override
    public void releaseSeats(Long showId, List<Long> seatIds) {
        List<String> keys = seatIds.stream()
                .map(seatId -> buildSeatLockKey(showId, seatId))
                .toList();
        redisTemplate.delete(keys);
        log.info("Released {} seats for show {}", seatIds.size(), showId);
    }

    @Override
    public void releaseSeatsByUser(Long showId, Long userId) {
        String userSeatsKey = buildUserSeatsKey(showId, userId);
        Set<String> seatIds = redisTemplate.opsForSet().members(userSeatsKey);

        if (seatIds != null && !seatIds.isEmpty()) {
            List<String> lockKeys = seatIds.stream()
                    .map(seatId -> buildSeatLockKey(showId, Long.parseLong(seatId)))
                    .toList();

            // Lua script: only delete if lock belongs to this user
            String luaScript = """
                local userId = ARGV[1]
                local deleted = 0
                for i, key in ipairs(KEYS) do
                    if redis.call('GET', key) == userId then
                        redis.call('DEL', key)
                        deleted = deleted + 1
                    end
                end
                return deleted
                """;

            redisTemplate.execute(
                    RedisScript.of(luaScript, Long.class),
                    lockKeys,
                    userId.toString()
            );

            redisTemplate.delete(userSeatsKey);
        }
        log.info("Released all seats for user {} in show {}", userId, showId);
    }

    @Override
    public Set<Long> getLockedSeats(Long showId) {
        String pattern = SEAT_LOCK_PREFIX + showId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            return Collections.emptySet();
        }

        return keys.stream()
                .map(key -> Long.parseLong(key.substring(key.lastIndexOf(":") + 1)))
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean isSeatAvailable(Long showId, Long seatId) {
        String lockKey = buildSeatLockKey(showId, seatId);
        return !Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    private String buildSeatLockKey(Long showId, Long seatId) {
        return SEAT_LOCK_PREFIX + showId + ":" + seatId;
    }

    private String buildUserSeatsKey(Long showId, Long userId) {
        return USER_SEATS_PREFIX + showId + ":" + userId;
    }
}
```

---

## 7. BOOKING SERVICE IMPLEMENTATION

```java
package com.razkart.cinehub.booking.service;

import com.razkart.cinehub.booking.dto.*;
import com.razkart.cinehub.booking.entity.*;
import com.razkart.cinehub.booking.repository.*;
import com.razkart.cinehub.common.exception.*;
import com.razkart.cinehub.show.entity.Show;
import com.razkart.cinehub.show.entity.ShowPricing;
import com.razkart.cinehub.show.repository.ShowRepository;
import com.razkart.cinehub.user.entity.User;
import com.razkart.cinehub.venue.entity.Seat;
import com.razkart.cinehub.venue.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final TicketRepository ticketRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final SeatLockService seatLockService;

    @Value("${cinehub.booking.expiry-minutes:10}")
    private int bookingExpiryMinutes;

    @Value("${cinehub.booking.max-seats-per-booking:10}")
    private int maxSeatsPerBooking;

    @Value("${cinehub.booking.convenience-fee-percent:5.0}")
    private BigDecimal convenienceFeePercent;

    @Value("${cinehub.booking.tax-percent:18.0}")
    private BigDecimal taxPercent;

    @Override
    @Transactional
    public BookingResponse initiateBooking(BookingRequest request, User user) {
        log.info("Initiating booking for user: {}, show: {}", user.getId(), request.showId());

        // 1. Validate show exists and is bookable
        Show show = showRepository.findById(request.showId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + request.showId()));

        if (!show.isBookable()) {
            throw new BusinessException("Show is not available for booking");
        }

        // 2. Validate seat count
        if (request.seatIds().size() > maxSeatsPerBooking) {
            throw new BusinessException("Maximum " + maxSeatsPerBooking + " seats allowed per booking");
        }

        // 3. Check for duplicate booking
        if (bookingRepository.existsByUserAndShowAndStatusIn(user, show,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED))) {
            throw new BusinessException("You already have a booking for this show");
        }

        // 4. Lock seats in Redis
        List<Long> lockedSeats = seatLockService.lockSeats(
                show.getId(),
                request.seatIds(),
                user.getId(),
                bookingExpiryMinutes
        );

        if (lockedSeats.size() != request.seatIds().size()) {
            seatLockService.releaseSeatsByUser(show.getId(), user.getId());
            throw new SeatNotAvailableException("Some selected seats are no longer available");
        }

        try {
            // 5. Fetch seats and calculate pricing
            List<Seat> seats = seatRepository.findAllById(request.seatIds());
            Map<String, BigDecimal> seatTypePrices = getPricingMap(show);
            PricingDetail pricing = calculatePricing(seats, seatTypePrices);

            // 6. Create booking
            Booking booking = Booking.builder()
                    .user(user)
                    .show(show)
                    .totalAmount(pricing.ticketAmount())
                    .convenienceFee(pricing.convenienceFee())
                    .taxAmount(pricing.taxAmount())
                    .finalAmount(pricing.finalAmount())
                    .expiresAt(LocalDateTime.now().plusMinutes(bookingExpiryMinutes))
                    .build();

            // 7. Add booked seats
            for (Seat seat : seats) {
                BigDecimal seatPrice = seatTypePrices.get(seat.getSeatType().name());
                BookedSeat bookedSeat = BookedSeat.builder()
                        .show(show)
                        .seat(seat)
                        .seatLabel(seat.getSeatLabel())
                        .price(seatPrice)
                        .build();
                booking.addSeat(bookedSeat);
            }

            // 8. Save booking
            Booking savedBooking = bookingRepository.save(booking);

            log.info("Booking initiated: {}", savedBooking.getBookingNumber());
            return mapToResponse(savedBooking);

        } catch (Exception e) {
            seatLockService.releaseSeatsByUser(show.getId(), user.getId());
            throw e;
        }
    }

    @Override
    @Transactional
    public BookingResponse confirmBooking(Long bookingId, PaymentConfirmation payment) {
        log.info("Confirming booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Booking is not in pending state");
        }

        if (booking.isExpired()) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            seatLockService.releaseSeatsByUser(booking.getShow().getId(), booking.getUser().getId());
            throw new BusinessException("Booking has expired");
        }

        // Verify payment amount
        if (payment.amount().compareTo(booking.getFinalAmount()) != 0) {
            throw new BusinessException("Payment amount mismatch");
        }

        // Confirm booking
        booking.confirm();

        // Generate tickets
        generateTickets(booking);

        Booking confirmedBooking = bookingRepository.save(booking);

        log.info("Booking confirmed: {}", confirmedBooking.getBookingNumber());
        return mapToResponse(confirmedBooking);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId, String reason) {
        log.info("Cancelling booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException("Not authorized to cancel this booking");
        }

        booking.cancel(reason);

        // Invalidate tickets
        booking.getTickets().forEach(ticket -> ticket.setStatus(TicketStatus.CANCELLED));

        // Release seat locks
        List<Long> seatIds = booking.getBookedSeats().stream()
                .map(bs -> bs.getSeat().getId())
                .toList();
        seatLockService.releaseSeats(booking.getShow().getId(), seatIds);

        Booking cancelledBooking = bookingRepository.save(booking);

        log.info("Booking cancelled: {}", cancelledBooking.getBookingNumber());
        return mapToResponse(cancelledBooking);
    }

    @Override
    public BookingResponse getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException("Not authorized to view this booking");
        }

        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SeatAvailabilityResponse getAvailableSeats(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + showId));

        List<Seat> allSeats = seatRepository.findByScreenId(show.getScreen().getId());
        Set<Long> lockedSeatIds = seatLockService.getLockedSeats(showId);
        Set<Long> bookedSeatIds = bookedSeatRepository.findBookedSeatIdsByShowId(showId);

        Set<Long> unavailableSeatIds = new HashSet<>();
        unavailableSeatIds.addAll(lockedSeatIds);
        unavailableSeatIds.addAll(bookedSeatIds);

        Map<String, BigDecimal> pricing = getPricingMap(show);

        return new SeatAvailabilityResponse(showId, allSeats, unavailableSeatIds, pricing);
    }

    // Helper methods
    private Map<String, BigDecimal> getPricingMap(Show show) {
        Map<String, BigDecimal> pricing = new HashMap<>();
        for (ShowPricing sp : show.getPricing()) {
            pricing.put(sp.getSeatType().name(), sp.getPrice());
        }
        return pricing;
    }

    private PricingDetail calculatePricing(List<Seat> seats, Map<String, BigDecimal> seatTypePrices) {
        BigDecimal ticketAmount = seats.stream()
                .map(seat -> seatTypePrices.getOrDefault(seat.getSeatType().name(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal convenienceFee = ticketAmount
                .multiply(convenienceFeePercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal subtotal = ticketAmount.add(convenienceFee);

        BigDecimal taxAmount = subtotal
                .multiply(taxPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal finalAmount = subtotal.add(taxAmount);

        return new PricingDetail(ticketAmount, convenienceFee, taxAmount, BigDecimal.ZERO, finalAmount);
    }

    private void generateTickets(Booking booking) {
        for (BookedSeat seat : booking.getBookedSeats()) {
            Ticket ticket = Ticket.builder()
                    .booking(booking)
                    .seatLabel(seat.getSeatLabel())
                    .ticketNumber(generateTicketNumber())
                    .qrCode(generateQRCode(booking, seat))
                    .status(TicketStatus.VALID)
                    .build();
            booking.getTickets().add(ticket);
        }
    }

    private String generateTicketNumber() {
        return "TKT" + System.currentTimeMillis() +
                String.format("%06d", (int) (Math.random() * 1000000));
    }

    private String generateQRCode(Booking booking, BookedSeat seat) {
        String data = booking.getBookingNumber() + "|" + seat.getSeatLabel();
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    private BookingResponse mapToResponse(Booking booking) {
        // Map booking entity to response DTO
        // Implementation depends on your DTO structure
        return null; // TODO: Implement mapping
    }
}
```

---

## 8. API ENDPOINTS

### Booking Controller

```java
package com.razkart.cinehub.booking.controller;

import com.razkart.cinehub.booking.dto.*;
import com.razkart.cinehub.booking.service.BookingService;
import com.razkart.cinehub.common.dto.ApiResponse;
import com.razkart.cinehub.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Booking management APIs")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Initiate a new booking")
    public ResponseEntity<ApiResponse<BookingResponse>> initiateBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.initiateBooking(request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(booking, "Booking initiated. Complete payment within 10 minutes."));
    }

    @PostMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm booking after payment")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody PaymentConfirmation payment) {

        BookingResponse booking = bookingService.confirmBooking(bookingId, payment);
        return ResponseEntity.ok(ApiResponse.success(booking, "Booking confirmed successfully"));
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody(required = false) CancellationRequest request,
            @AuthenticationPrincipal User currentUser) {

        String reason = request != null ? request.reason() : "User requested cancellation";
        BookingResponse booking = bookingService.cancelBooking(bookingId, currentUser.getId(), reason);
        return ResponseEntity.ok(ApiResponse.success(booking, "Booking cancelled"));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking details")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.getBooking(bookingId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @GetMapping
    @Operation(summary = "Get user's bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUserBookings(
            @AuthenticationPrincipal User currentUser) {

        List<BookingResponse> bookings = bookingService.getUserBookings(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/shows/{showId}/seats")
    @Operation(summary = "Get available seats for a show")
    public ResponseEntity<ApiResponse<SeatAvailabilityResponse>> getAvailableSeats(
            @PathVariable Long showId) {

        SeatAvailabilityResponse availability = bookingService.getAvailableSeats(showId);
        return ResponseEntity.ok(ApiResponse.success(availability));
    }
}
```

---

## 9. API RESPONSE WRAPPER

```java
package com.razkart.cinehub.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Object errors;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Object errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
}
```

---

## 10. GLOBAL EXCEPTION HANDLER

```java
package com.razkart.cinehub.common.exception;

import com.razkart.cinehub.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleSeatNotAvailable(SeatNotAvailableException ex) {
        log.warn("Seat not available: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }
}
```

---

## 11. DOCKER COMPOSE (Local Development)

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: cinehub-mysql
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: cinehub
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: --default-authentication-plugin=mysql_native_password
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: cinehub-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes

  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: cinehub-rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

volumes:
  mysql_data:
  redis_data:
  rabbitmq_data:
```

---

## 12. IMPLEMENTATION INSTRUCTIONS FOR CLAUDE CODE

When implementing this project, follow these priorities:

### Phase 1: Foundation
1. Create the package structure as defined above
2. Add all Maven dependencies to pom.xml
3. Create application.yml configuration
4. Set up Flyway migrations (all V1-V6 SQL files)
5. Create base entity and auditing configuration

### Phase 2: User Module
1. Create User entity with all fields
2. Create UserRepository
3. Implement UserService with registration, login
4. Create JWT authentication
5. Create UserController with endpoints

### Phase 3: Event & Venue Modules
1. Create all Event entities (Event, Cast, enums)
2. Create all Venue entities (City, Venue, Screen, Seat)
3. Create repositories for each entity
4. Implement services with CRUD operations
5. Create controllers with REST endpoints

### Phase 4: Show Module
1. Create Show and ShowPricing entities
2. Create repositories
3. Implement ShowService
4. Create ShowController

### Phase 5: Booking Module (Critical)
1. Create Booking, BookedSeat, Ticket entities
2. Create repositories
3. Implement SeatLockService with Redis
4. Implement BookingService with all business logic
5. Create BookingController
6. Test concurrency scenarios

### Phase 6: Payment Module
1. Create Payment, Refund entities
2. Implement payment gateway integration
3. Create PaymentController

### Phase 7: Search & Notification
1. Implement search using MySQL full-text
2. Set up RabbitMQ for async notifications
3. Implement email/SMS services

---

## KEY IMPLEMENTATION NOTES

1. **Always use BIGINT for primary keys** - Better MySQL performance
2. **Use @Version for optimistic locking** - On Booking and User entities
3. **Redis SETNX for seat locking** - Critical for preventing double booking
4. **UNIQUE constraint on (show_id, seat_id)** - Database-level protection
5. **Idempotency keys for payments** - Prevent duplicate charges
6. **Use records for DTOs** - Java 21 feature, immutable by default
7. **Validate all inputs** - Use @Valid and custom validators
8. **Log important operations** - Especially booking flow
9. **Handle exceptions gracefully** - Use GlobalExceptionHandler
10. **Write tests for booking service** - Critical business logic

---

This context file should be placed in your project root and referenced when working with Claude Code.
