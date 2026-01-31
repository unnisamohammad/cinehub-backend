# CineHub - Movie Ticketing Platform Architecture

## Table of Contents
1. [Executive Summary](#executive-summary)
2. [High-Level Design (HLD)](#high-level-design-hld)
3. [Low-Level Design (LLD)](#low-level-design-lld)
4. [Database Design](#database-design)
5. [API Design](#api-design)
6. [Implementation Roadmap](#implementation-roadmap)

---

## Executive Summary

**CineHub** is a movie ticketing platform similar to BookMyShow. This document provides a comprehensive architectural blueprint following industry standards.

### Technology Stack
| Layer | Technology |
|-------|------------|
| Frontend | React 18+ |
| Backend | Spring Boot 4.0.2 |
| Language | Java 21 |
| Database | MySQL 8.x |
| Security | Spring Security + JWT |
| Build | Maven |
| API Docs | OpenAPI 3.0 (Springdoc) |

### Design Principles Applied
- **SOLID Principles** - Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
- **KISS** - Keep It Simple, Stupid
- **DRY** - Don't Repeat Yourself
- **Clean Architecture** - Separation of concerns with clear boundaries
- **Domain-Driven Design (DDD)** - Business logic centered around domain models

---

## High-Level Design (HLD)

### 1. System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           REACT FRONTEND                                 │
│                    (Single Page Application)                             │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ HTTPS/REST
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         API GATEWAY / LOAD BALANCER                      │
│                         (Future: Spring Cloud Gateway)                   │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        SPRING BOOT APPLICATION                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │ Controller  │  │  Service    │  │ Repository  │  │   Config    │    │
│  │   Layer     │──│   Layer     │──│   Layer     │  │   Layer     │    │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │
│                                                                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │  Security   │  │  Exception  │  │    DTO      │  │   Entity    │    │
│  │   Filter    │  │   Handler   │  │   Mapper    │  │   Models    │    │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                            MySQL DATABASE                                │
│                         (Master-Replica Setup)                           │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2. Core Modules/Domains

```
┌────────────────────────────────────────────────────────────────────┐
│                         CINEHUB DOMAINS                             │
├──────────────┬──────────────┬──────────────┬──────────────────────┤
│     USER     │    MOVIE     │   THEATER    │      BOOKING         │
│   MANAGEMENT │  MANAGEMENT  │  MANAGEMENT  │     MANAGEMENT       │
├──────────────┼──────────────┼──────────────┼──────────────────────┤
│ • Register   │ • Add Movie  │ • Add Theater│ • Search Shows       │
│ • Login      │ • List Movies│ • Add Screens│ • Select Seats       │
│ • Profile    │ • Categories │ • Add Shows  │ • Payment            │
│ • Roles      │ • Languages  │ • Seat Layout│ • Confirmation       │
│ • Addresses  │ • Reviews    │ • Cities     │ • Cancellation       │
└──────────────┴──────────────┴──────────────┴──────────────────────┘
```

### 3. User Roles & Permissions

| Role | Permissions |
|------|-------------|
| **GUEST** | Browse movies, theaters, shows (Read-only) |
| **CUSTOMER** | All Guest + Book tickets, Manage profile, View booking history |
| **THEATER_ADMIN** | Manage own theater, screens, shows, view theater bookings |
| **SUPER_ADMIN** | Full system access, User management, All theater management |

### 4. Core Business Flows

#### 4.1 User Registration & Authentication Flow
```
User → Register → Validate → Save → Send Email → Login → JWT Token → Access
```

#### 4.2 Movie Booking Flow (Critical Path)
```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  Search  │ →  │  Select  │ →  │  Select  │ →  │  Select  │ →  │ Checkout │
│  Movies  │    │  Movie   │    │   Show   │    │  Seats   │    │ & Pay    │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
                                                      │
                                            ┌─────────┴─────────┐
                                            │  SEAT LOCK (5min) │
                                            │  Prevents double  │
                                            │     booking       │
                                            └───────────────────┘
```

#### 4.3 Seat Booking - Concurrency Handling
```
                    ┌────────────────────────────┐
                    │     User Selects Seats     │
                    └────────────┬───────────────┘
                                 │
                    ┌────────────▼───────────────┐
                    │   Check Seat Availability  │
                    │   (Optimistic Locking)     │
                    └────────────┬───────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
     ┌────────▼────────┐ ┌──────▼──────┐  ┌───────▼───────┐
     │   AVAILABLE     │ │   LOCKED    │  │    BOOKED     │
     │ → Lock for 5min │ │ → Show Timer│  │ → Show Error  │
     └────────┬────────┘ └─────────────┘  └───────────────┘
              │
     ┌────────▼────────┐
     │ Payment Process │
     └────────┬────────┘
              │
    ┌─────────┴─────────┐
    │                   │
┌───▼───┐          ┌────▼────┐
│SUCCESS│          │ FAILURE │
│→BOOKED│          │→RELEASE │
└───────┘          └─────────┘
```

---

## Low-Level Design (LLD)

### 1. Package Structure

```
src/main/java/com/razkart/cinehub/
│
├── CineHubApplication.java              # Main Application Entry
│
├── common/                              # Cross-cutting concerns
│   ├── constants/
│   │   └── AppConstants.java            # Application constants
│   ├── enums/
│   │   ├── BookingStatus.java
│   │   ├── PaymentStatus.java
│   │   ├── SeatStatus.java
│   │   └── UserRole.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   │   ├── ResourceNotFoundException.java
│   │   ├── SeatAlreadyBookedException.java
│   │   ├── PaymentFailedException.java
│   │   └── UnauthorizedException.java
│   └── util/
│       ├── DateTimeUtil.java
│       └── BookingReferenceGenerator.java
│
├── config/                              # Configuration classes
│   ├── SecurityConfig.java              # Spring Security config
│   ├── JwtConfig.java                   # JWT configuration
│   ├── CorsConfig.java                  # CORS for React frontend
│   ├── OpenApiConfig.java               # Swagger/OpenAPI config
│   └── AuditConfig.java                 # JPA Auditing config
│
├── security/                            # Security components
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
│
├── domain/                              # Domain layer (Entities)
│   ├── base/
│   │   └── BaseEntity.java              # Common auditing fields
│   ├── user/
│   │   ├── User.java
│   │   ├── Role.java
│   │   └── Address.java
│   ├── movie/
│   │   ├── Movie.java
│   │   ├── Genre.java
│   │   ├── Language.java
│   │   └── Review.java
│   ├── theater/
│   │   ├── City.java
│   │   ├── Theater.java
│   │   ├── Screen.java
│   │   ├── Seat.java
│   │   └── Show.java
│   └── booking/
│       ├── Booking.java
│       ├── BookedSeat.java
│       └── Payment.java
│
├── repository/                          # Data Access Layer
│   ├── UserRepository.java
│   ├── MovieRepository.java
│   ├── TheaterRepository.java
│   ├── ScreenRepository.java
│   ├── ShowRepository.java
│   ├── SeatRepository.java
│   ├── BookingRepository.java
│   └── PaymentRepository.java
│
├── dto/                                 # Data Transfer Objects
│   ├── request/
│   │   ├── auth/
│   │   │   ├── LoginRequest.java
│   │   │   └── RegisterRequest.java
│   │   ├── movie/
│   │   │   └── MovieRequest.java
│   │   ├── theater/
│   │   │   ├── TheaterRequest.java
│   │   │   ├── ScreenRequest.java
│   │   │   └── ShowRequest.java
│   │   └── booking/
│   │       ├── SeatSelectionRequest.java
│   │       └── BookingRequest.java
│   ├── response/
│   │   ├── ApiResponse.java             # Generic API response wrapper
│   │   ├── PagedResponse.java           # Pagination wrapper
│   │   ├── auth/
│   │   │   ├── JwtResponse.java
│   │   │   └── UserResponse.java
│   │   ├── movie/
│   │   │   ├── MovieResponse.java
│   │   │   └── MovieDetailResponse.java
│   │   ├── theater/
│   │   │   ├── TheaterResponse.java
│   │   │   ├── ShowResponse.java
│   │   │   └── SeatLayoutResponse.java
│   │   └── booking/
│   │       ├── BookingResponse.java
│   │       └── PaymentResponse.java
│   └── mapper/                          # Entity ↔ DTO mappers
│       ├── UserMapper.java
│       ├── MovieMapper.java
│       ├── TheaterMapper.java
│       └── BookingMapper.java
│
├── service/                             # Business Logic Layer
│   ├── AuthService.java
│   ├── UserService.java
│   ├── MovieService.java
│   ├── TheaterService.java
│   ├── ShowService.java
│   ├── SeatService.java
│   ├── BookingService.java
│   └── PaymentService.java
│
└── controller/                          # REST API Layer
    ├── AuthController.java
    ├── UserController.java
    ├── MovieController.java
    ├── TheaterController.java
    ├── ShowController.java
    ├── BookingController.java
    └── AdminController.java
```

### 2. Core Entity Designs

#### 2.1 BaseEntity (Auditing)
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @Version  // Optimistic locking
    private Long version;
}
```

#### 2.2 User Entity
```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String email;        // Unique
    private String password;     // BCrypt encoded
    private String firstName;
    private String lastName;
    private String phone;
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<Address> addresses;
}
```

#### 2.3 Movie Entity
```java
@Entity
@Table(name = "movies")
public class Movie extends BaseEntity {
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private Double rating;

    @Enumerated(EnumType.STRING)
    private MovieCertificate certificate;  // U, UA, A, S

    @ManyToMany
    private Set<Genre> genres;

    @ManyToMany
    private Set<Language> languages;
}
```

#### 2.4 Theater & Screen Entities
```java
@Entity
@Table(name = "theaters")
public class Theater extends BaseEntity {
    private String name;
    private String address;

    @ManyToOne
    private City city;

    @ManyToOne
    private User owner;  // Theater admin

    @OneToMany(mappedBy = "theater")
    private List<Screen> screens;
}

@Entity
@Table(name = "screens")
public class Screen extends BaseEntity {
    private String name;  // "Screen 1", "IMAX"
    private Integer totalSeats;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;  // REGULAR, IMAX, 4DX

    @ManyToOne
    private Theater theater;

    @OneToMany(mappedBy = "screen")
    private List<Seat> seats;
}
```

#### 2.5 Seat Entity (Critical for Layout)
```java
@Entity
@Table(name = "seats")
public class Seat extends BaseEntity {
    private String rowName;      // "A", "B", "C"
    private Integer seatNumber;  // 1, 2, 3

    @Enumerated(EnumType.STRING)
    private SeatType seatType;   // REGULAR, PREMIUM, RECLINER

    private Double basePrice;

    @ManyToOne
    private Screen screen;
}
```

#### 2.6 Show Entity
```java
@Entity
@Table(name = "shows")
public class Show extends BaseEntity {
    @ManyToOne
    private Movie movie;

    @ManyToOne
    private Screen screen;

    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private Double priceMultiplier;  // 1.0 normal, 1.5 weekend
    private boolean active;
}
```

#### 2.7 Booking & BookedSeat Entities
```java
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {
    private String bookingReference;  // "CNH-ABC123"

    @ManyToOne
    private User user;

    @ManyToOne
    private Show show;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;  // PENDING, CONFIRMED, CANCELLED

    private Double totalAmount;
    private Double convenienceFee;
    private Double taxAmount;
    private Double finalAmount;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookedSeat> bookedSeats;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;
}

@Entity
@Table(name = "booked_seats")
public class BookedSeat extends BaseEntity {
    @ManyToOne
    private Booking booking;

    @ManyToOne
    private Seat seat;

    private Double price;  // Price at time of booking

    @Enumerated(EnumType.STRING)
    private SeatStatus status;  // LOCKED, BOOKED, RELEASED

    private LocalDateTime lockExpiresAt;  // For temporary lock
}
```

### 3. Repository Interfaces (Spring Data JPA)

```java
// Using Spring Data JPA - minimal code!

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Spring generates the query automatically
    List<Movie> findByTitleContainingIgnoreCase(String title);

    // Custom query for shows in a city
    @Query("""
        SELECT DISTINCT m FROM Movie m
        JOIN Show s ON s.movie = m
        JOIN Screen sc ON s.screen = sc
        JOIN Theater t ON sc.theater = t
        WHERE t.city.id = :cityId
        AND s.showDate >= :date
        AND s.active = true
        """)
    List<Movie> findMoviesInCity(@Param("cityId") Long cityId,
                                  @Param("date") LocalDate date);
}

public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieIdAndScreenTheaterCityIdAndShowDate(
        Long movieId, Long cityId, LocalDate date);

    @Query("""
        SELECT s FROM Show s
        WHERE s.movie.id = :movieId
        AND s.showDate = :date
        AND s.screen.theater.id = :theaterId
        ORDER BY s.startTime
        """)
    List<Show> findShowsForMovieInTheater(
        @Param("movieId") Long movieId,
        @Param("theaterId") Long theaterId,
        @Param("date") LocalDate date);
}

public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {

    // Find booked/locked seats for a show
    @Query("""
        SELECT bs FROM BookedSeat bs
        WHERE bs.booking.show.id = :showId
        AND (bs.status = 'BOOKED'
             OR (bs.status = 'LOCKED' AND bs.lockExpiresAt > :now))
        """)
    List<BookedSeat> findUnavailableSeatsForShow(
        @Param("showId") Long showId,
        @Param("now") LocalDateTime now);
}
```

### 4. Service Layer Design

```java
// BookingService - Most critical service
@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final PaymentService paymentService;
    private final BookingMapper mapper;

    // Step 1: Lock seats temporarily (5 minutes)
    public BookingResponse lockSeats(Long showId,
                                      List<Long> seatIds,
                                      Long userId) {
        // Validate show exists and is active
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new ResourceNotFoundException("Show", showId));

        // Check seat availability (with pessimistic lock)
        List<BookedSeat> unavailable = bookedSeatRepository
            .findUnavailableSeatsForShow(showId, LocalDateTime.now());

        Set<Long> unavailableSeatIds = unavailable.stream()
            .map(bs -> bs.getSeat().getId())
            .collect(Collectors.toSet());

        // Check if any requested seat is unavailable
        boolean hasConflict = seatIds.stream()
            .anyMatch(unavailableSeatIds::contains);

        if (hasConflict) {
            throw new SeatAlreadyBookedException(
                "One or more seats are no longer available");
        }

        // Create booking with PENDING status
        Booking booking = createPendingBooking(show, userId, seatIds);

        return mapper.toResponse(bookingRepository.save(booking));
    }

    // Step 2: Confirm booking after payment
    public BookingResponse confirmBooking(Long bookingId,
                                           PaymentRequest paymentRequest) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        // Verify booking is still valid (not expired)
        if (isBookingExpired(booking)) {
            releaseSeats(booking);
            throw new BookingExpiredException("Booking session expired");
        }

        // Process payment
        Payment payment = paymentService.processPayment(booking, paymentRequest);

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.getBookedSeats()
                .forEach(bs -> bs.setStatus(SeatStatus.BOOKED));
        } else {
            releaseSeats(booking);
            throw new PaymentFailedException("Payment failed");
        }

        return mapper.toResponse(bookingRepository.save(booking));
    }

    // Scheduled job to release expired locks
    @Scheduled(fixedRate = 60000)  // Every minute
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        List<BookedSeat> expiredLocks = bookedSeatRepository
            .findByStatusAndLockExpiresAtBefore(SeatStatus.LOCKED, now);

        expiredLocks.forEach(bs -> bs.setStatus(SeatStatus.RELEASED));
        bookedSeatRepository.saveAll(expiredLocks);
    }
}
```

### 5. Controller Layer Design

```java
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Booking management APIs")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/lock-seats")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Lock seats for booking")
    public ResponseEntity<ApiResponse<BookingResponse>> lockSeats(
            @Valid @RequestBody SeatSelectionRequest request,
            @AuthenticationPrincipal UserDetails user) {

        BookingResponse booking = bookingService.lockSeats(
            request.getShowId(),
            request.getSeatIds(),
            getUserId(user)
        );

        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PostMapping("/{bookingId}/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Confirm booking with payment")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody PaymentRequest paymentRequest,
            @AuthenticationPrincipal UserDetails user) {

        BookingResponse booking = bookingService.confirmBooking(
            bookingId, paymentRequest);

        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success(
            bookingService.getUserBookings(getUserId(user), pageable)));
    }
}
```

---

## Database Design

### Entity Relationship Diagram (ERD)

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│      USERS      │       │      ROLES      │       │   USER_ROLES    │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ PK id           │       │ PK id           │       │ FK user_id      │
│    email        │       │    name         │       │ FK role_id      │
│    password     │       │    description  │       └─────────────────┘
│    first_name   │       └─────────────────┘
│    last_name    │
│    phone        │       ┌─────────────────┐
│    active       │       │    ADDRESSES    │
│    created_at   │       ├─────────────────┤
│    updated_at   │       │ PK id           │
└────────┬────────┘       │ FK user_id      │
         │                │    street       │
         └────────────────│    city         │
                          │    state        │
                          │    pincode      │
                          └─────────────────┘

┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     MOVIES      │       │     GENRES      │       │  MOVIE_GENRES   │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ PK id           │       │ PK id           │       │ FK movie_id     │
│    title        │───────│    name         │───────│ FK genre_id     │
│    description  │       └─────────────────┘       └─────────────────┘
│    duration_min │
│    release_date │       ┌─────────────────┐       ┌─────────────────┐
│    poster_url   │       │   LANGUAGES     │       │ MOVIE_LANGUAGES │
│    trailer_url  │       ├─────────────────┤       ├─────────────────┤
│    rating       │       │ PK id           │       │ FK movie_id     │
│    certificate  │───────│    name         │───────│ FK language_id  │
│    created_at   │       │    code         │       └─────────────────┘
└─────────────────┘       └─────────────────┘

┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     CITIES      │       │    THEATERS     │       │     SCREENS     │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ PK id           │       │ PK id           │       │ PK id           │
│    name         │───────│ FK city_id      │───────│ FK theater_id   │
│    state        │       │ FK owner_id     │       │    name         │
└─────────────────┘       │    name         │       │    screen_type  │
                          │    address      │       │    total_seats  │
                          │    active       │       └────────┬────────┘
                          └─────────────────┘                │
                                                             │
                          ┌─────────────────┐       ┌────────┴────────┐
                          │      SHOWS      │       │      SEATS      │
                          ├─────────────────┤       ├─────────────────┤
                          │ PK id           │       │ PK id           │
                          │ FK movie_id     │       │ FK screen_id    │
                          │ FK screen_id    │       │    row_name     │
                          │    show_date    │       │    seat_number  │
                          │    start_time   │       │    seat_type    │
                          │    end_time     │       │    base_price   │
                          │    price_multi  │       └─────────────────┘
                          │    active       │
                          └────────┬────────┘
                                   │
┌─────────────────┐       ┌────────┴────────┐       ┌─────────────────┐
│    PAYMENTS     │       │    BOOKINGS     │       │  BOOKED_SEATS   │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ PK id           │       │ PK id           │       │ PK id           │
│ FK booking_id   │───────│ FK user_id      │───────│ FK booking_id   │
│    amount       │       │ FK show_id      │       │ FK seat_id      │
│    method       │       │    booking_ref  │       │    price        │
│    txn_id       │       │    status       │       │    status       │
│    status       │       │    total_amount │       │    lock_expires │
│    created_at   │       │    conv_fee     │       └─────────────────┘
└─────────────────┘       │    tax_amount   │
                          │    final_amount │
                          │    created_at   │
                          └─────────────────┘
```

### Key Database Indexes

```sql
-- Performance-critical indexes
CREATE INDEX idx_shows_movie_date ON shows(movie_id, show_date);
CREATE INDEX idx_shows_screen_date ON shows(screen_id, show_date);
CREATE INDEX idx_theaters_city ON theaters(city_id);
CREATE INDEX idx_booked_seats_show_status ON booked_seats(booking_id, status);
CREATE INDEX idx_bookings_user ON bookings(user_id, created_at DESC);
CREATE INDEX idx_bookings_reference ON bookings(booking_reference);
```

---

## API Design

### REST API Endpoints

#### Authentication APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | User registration |
| POST | `/api/v1/auth/login` | User login, returns JWT |
| POST | `/api/v1/auth/refresh` | Refresh JWT token |
| POST | `/api/v1/auth/logout` | Logout (invalidate token) |

#### Movie APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/movies` | List all movies (paginated) |
| GET | `/api/v1/movies/{id}` | Get movie details |
| GET | `/api/v1/movies/now-showing?cityId=1` | Movies currently showing |
| GET | `/api/v1/movies/coming-soon` | Upcoming movies |
| GET | `/api/v1/movies/{id}/shows?cityId=1&date=2024-01-15` | Shows for movie |
| POST | `/api/v1/movies` | Add movie (ADMIN) |
| PUT | `/api/v1/movies/{id}` | Update movie (ADMIN) |

#### Theater APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/cities` | List all cities |
| GET | `/api/v1/theaters?cityId=1` | Theaters in city |
| GET | `/api/v1/theaters/{id}` | Theater details |
| GET | `/api/v1/theaters/{id}/shows?date=2024-01-15` | All shows in theater |
| POST | `/api/v1/theaters` | Add theater (ADMIN) |

#### Show APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/shows/{id}` | Show details |
| GET | `/api/v1/shows/{id}/seats` | Seat layout with availability |
| POST | `/api/v1/shows` | Create show (THEATER_ADMIN) |

#### Booking APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/bookings/lock-seats` | Lock seats (start booking) |
| POST | `/api/v1/bookings/{id}/confirm` | Confirm with payment |
| GET | `/api/v1/bookings/{id}` | Get booking details |
| GET | `/api/v1/bookings/my-bookings` | User's booking history |
| POST | `/api/v1/bookings/{id}/cancel` | Cancel booking |

### API Response Format

```json
// Success Response
{
    "success": true,
    "message": "Operation successful",
    "data": { ... },
    "timestamp": "2024-01-15T10:30:00Z"
}

// Error Response
{
    "success": false,
    "message": "Seat already booked",
    "errorCode": "SEAT_UNAVAILABLE",
    "errors": [
        {"field": "seatIds", "message": "Seats A1, A2 are no longer available"}
    ],
    "timestamp": "2024-01-15T10:30:00Z"
}

// Paginated Response
{
    "success": true,
    "data": {
        "content": [ ... ],
        "page": 0,
        "size": 10,
        "totalElements": 100,
        "totalPages": 10,
        "last": false
    }
}
```

---

## Implementation Roadmap

### Phase 1: Foundation (Week 1-2)
**Goal:** Setup project structure, database, and authentication

| Task | Description | Priority |
|------|-------------|----------|
| 1.1 | Update pom.xml with all dependencies | HIGH |
| 1.2 | Configure MySQL datasource | HIGH |
| 1.3 | Create BaseEntity with auditing | HIGH |
| 1.4 | Implement User, Role entities | HIGH |
| 1.5 | Setup Spring Security + JWT | HIGH |
| 1.6 | Create Auth APIs (register/login) | HIGH |
| 1.7 | Configure CORS for React | HIGH |
| 1.8 | Setup Global Exception Handler | HIGH |

### Phase 2: Core Entities (Week 3-4)
**Goal:** Implement all domain entities and repositories

| Task | Description | Priority |
|------|-------------|----------|
| 2.1 | Create Movie, Genre, Language entities | HIGH |
| 2.2 | Create City, Theater, Screen entities | HIGH |
| 2.3 | Create Seat entity with layout support | HIGH |
| 2.4 | Create Show entity | HIGH |
| 2.5 | Implement all repositories | HIGH |
| 2.6 | Create DTOs and Mappers | HIGH |
| 2.7 | Add seed data (cities, genres) | MEDIUM |

### Phase 3: Movie & Theater Management (Week 5-6)
**Goal:** CRUD APIs for movies and theaters

| Task | Description | Priority |
|------|-------------|----------|
| 3.1 | MovieService + MovieController | HIGH |
| 3.2 | TheaterService + TheaterController | HIGH |
| 3.3 | ScreenService (with seat layout) | HIGH |
| 3.4 | ShowService + ShowController | HIGH |
| 3.5 | Search & Filter functionality | MEDIUM |
| 3.6 | Pagination implementation | MEDIUM |

### Phase 4: Booking System (Week 7-8) ⭐ Critical
**Goal:** Complete booking flow with seat locking

| Task | Description | Priority |
|------|-------------|----------|
| 4.1 | Seat availability check logic | CRITICAL |
| 4.2 | Seat locking mechanism (5 min) | CRITICAL |
| 4.3 | Booking creation flow | CRITICAL |
| 4.4 | Payment integration (mock) | HIGH |
| 4.5 | Booking confirmation | HIGH |
| 4.6 | Scheduled job: release expired locks | HIGH |
| 4.7 | Booking cancellation | MEDIUM |
| 4.8 | Booking history | MEDIUM |

### Phase 5: Testing & Polish (Week 9-10)
**Goal:** Testing, documentation, and edge cases

| Task | Description | Priority |
|------|-------------|----------|
| 5.1 | Unit tests for services | HIGH |
| 5.2 | Integration tests for booking flow | HIGH |
| 5.3 | OpenAPI documentation | MEDIUM |
| 5.4 | Performance testing | MEDIUM |
| 5.5 | Error handling refinement | HIGH |
| 5.6 | Logging implementation | MEDIUM |

---

## Edge Cases & Solutions

### 1. Concurrent Seat Booking
**Problem:** Two users try to book the same seat simultaneously.
**Solution:**
- Use optimistic locking (`@Version`) on BookedSeat entity
- Database-level unique constraint on (show_id, seat_id) for BOOKED status
- Return clear error message to second user

### 2. Payment Timeout
**Problem:** User's payment takes too long, seat lock expires.
**Solution:**
- Show countdown timer on frontend (4:30 remaining)
- Backend validates lock hasn't expired before confirming
- If expired, return `BOOKING_EXPIRED` error
- Frontend redirects to seat selection

### 3. Browser Closed During Booking
**Problem:** User closes browser after locking seats.
**Solution:**
- Scheduled job runs every minute
- Releases seats where `lockExpiresAt < now()`
- No orphaned locks

### 4. Show Time Passed
**Problem:** User tries to book for a show that already started.
**Solution:**
- Validate `show.startTime > now()` in booking service
- Hide past shows in API response
- Return `SHOW_ALREADY_STARTED` error

### 5. Double Submission
**Problem:** User clicks "Confirm" button multiple times.
**Solution:**
- Use idempotency key in request header
- Cache recent booking attempts (Redis/in-memory)
- Return same response for duplicate requests

### 6. Refund After Cancellation
**Problem:** User cancels confirmed booking.
**Solution:**
- Define cancellation policy (e.g., 2 hours before show)
- Calculate refund percentage based on policy
- Mark booking as CANCELLED
- Trigger refund process
- Release seats back to AVAILABLE
