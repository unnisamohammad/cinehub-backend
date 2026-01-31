# CineHub Implementation Guide

This guide provides step-by-step instructions for implementing the CineHub ticketing platform.

---

## Quick Start

### Prerequisites
1. Java 21 installed
2. MySQL 8.x running
3. Maven 3.9+ installed
4. IDE (IntelliJ IDEA recommended)

### Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Run the schema script
source src/main/resources/db/schema.sql
```

### Run Application
```bash
# Development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or with Maven wrapper
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Access Points
- **API Base URL:** http://localhost:8080/api
- **Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **Health Check:** http://localhost:8080/api/actuator/health

---

## Implementation Steps (Copy-Paste Ready Code)

### Step 1: Create Package Structure

Create these folders under `src/main/java/com/razkart/cinehub/`:

```
common/
  ├── constants/
  ├── enums/
  ├── exception/
  └── util/
config/
security/
domain/
  ├── base/
  ├── user/
  ├── movie/
  ├── theater/
  └── booking/
repository/
dto/
  ├── request/
  ├── response/
  └── mapper/
service/
controller/
```

---

### Step 2: Create Enums

**File: `common/enums/UserRole.java`**
```java
package com.razkart.cinehub.common.enums;

public enum UserRole {
    ROLE_GUEST,
    ROLE_CUSTOMER,
    ROLE_THEATER_ADMIN,
    ROLE_SUPER_ADMIN
}
```

**File: `common/enums/BookingStatus.java`**
```java
package com.razkart.cinehub.common.enums;

public enum BookingStatus {
    PENDING,      // Seats locked, awaiting payment
    CONFIRMED,    // Payment successful
    CANCELLED,    // Booking cancelled
    EXPIRED       // Lock expired without payment
}
```

**File: `common/enums/SeatStatus.java`**
```java
package com.razkart.cinehub.common.enums;

public enum SeatStatus {
    AVAILABLE,    // Seat can be booked
    LOCKED,       // Temporarily held (5 min)
    BOOKED,       // Confirmed booking
    RELEASED      // Released after lock expiry
}
```

**File: `common/enums/SeatType.java`**
```java
package com.razkart.cinehub.common.enums;

public enum SeatType {
    REGULAR,
    PREMIUM,
    RECLINER,
    VIP
}
```

**File: `common/enums/ScreenType.java`**
```java
package com.razkart.cinehub.common.enums;

public enum ScreenType {
    REGULAR,
    IMAX,
    DOLBY_ATMOS,
    _4DX,
    SCREEN_X
}
```

**File: `common/enums/PaymentStatus.java`**
```java
package com.razkart.cinehub.common.enums;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED
}
```

**File: `common/enums/MovieCertificate.java`**
```java
package com.razkart.cinehub.common.enums;

public enum MovieCertificate {
    U,      // Universal
    UA,     // Parental guidance for children under 12
    A,      // Adults only
    S       // Restricted to special classes
}
```

---

### Step 3: Create Base Entity

**File: `domain/base/BaseEntity.java`**
```java
package com.razkart.cinehub.domain.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @Version
    private Long version;
}
```

---

### Step 4: Create User Domain Entities

**File: `domain/user/Role.java`**
```java
package com.razkart.cinehub.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    private String description;
}
```

**File: `domain/user/User.java`**
```java
package com.razkart.cinehub.domain.user;

import com.razkart.cinehub.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String phone;

    @Builder.Default
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Address> addresses = new HashSet<>();

    // Helper method
    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }
}
```

**File: `domain/user/Address.java`**
```java
package com.razkart.cinehub.domain.user;

import com.razkart.cinehub.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private String addressType = "HOME";

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Builder.Default
    private boolean isDefault = false;
}
```

---

### Step 5: Create Movie Domain Entities

**File: `domain/movie/Genre.java`**
```java
package com.razkart.cinehub.domain.movie;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
```

**File: `domain/movie/Language.java`**
```java
package com.razkart.cinehub.domain.movie;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String code;
}
```

**File: `domain/movie/Movie.java`**
```java
package com.razkart.cinehub.domain.movie;

import com.razkart.cinehub.common.enums.MovieCertificate;
import com.razkart.cinehub.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer durationMinutes;

    private LocalDate releaseDate;

    private String posterUrl;

    private String trailerUrl;

    @Builder.Default
    private Double rating = 0.0;

    @Enumerated(EnumType.STRING)
    private MovieCertificate certificate;

    @Builder.Default
    private boolean active = true;

    @ManyToMany
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "movie_languages",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    @Builder.Default
    private Set<Language> languages = new HashSet<>();
}
```

---

### Step 6: Create Theater Domain Entities

**File: `domain/theater/City.java`**
```java
package com.razkart.cinehub.domain.theater;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String state;

    @Builder.Default
    private boolean active = true;
}
```

**File: `domain/theater/Theater.java`**
```java
package com.razkart.cinehub.domain.theater;

import com.razkart.cinehub.domain.base.BaseEntity;
import com.razkart.cinehub.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theaters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theater extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    private String phone;

    private String email;

    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Screen> screens = new ArrayList<>();
}
```

**File: `domain/theater/Screen.java`**
```java
package com.razkart.cinehub.domain.theater;

import com.razkart.cinehub.common.enums.ScreenType;
import com.razkart.cinehub.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScreenType screenType = ScreenType.REGULAR;

    private Integer totalRows;

    private Integer seatsPerRow;

    private Integer totalSeats;

    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();
}
```

**File: `domain/theater/Seat.java`**
```java
package com.razkart.cinehub.domain.theater;

import com.razkart.cinehub.common.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(nullable = false, length = 5)
    private String rowName;

    @Column(nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatType seatType = SeatType.REGULAR;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Builder.Default
    private boolean active = true;

    // Helper method to get seat label like "A1", "B5"
    public String getSeatLabel() {
        return rowName + seatNumber;
    }
}
```

**File: `domain/theater/Show.java`**
```java
package com.razkart.cinehub.domain.theater;

import com.razkart.cinehub.domain.base.BaseEntity;
import com.razkart.cinehub.domain.movie.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(nullable = false)
    private LocalDate showDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Builder.Default
    @Column(precision = 3, scale = 2)
    private BigDecimal priceMultiplier = BigDecimal.ONE;

    @Builder.Default
    private boolean active = true;
}
```

---

### Step 7: Create Booking Domain Entities

**File: `domain/booking/Booking.java`**
```java
package com.razkart.cinehub.domain.booking;

import com.razkart.cinehub.common.enums.BookingStatus;
import com.razkart.cinehub.domain.base.BaseEntity;
import com.razkart.cinehub.domain.theater.Show;
import com.razkart.cinehub.domain.user.User;
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

    @Column(nullable = false, unique = true, length = 20)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    private Integer totalSeats;

    @Column(precision = 10, scale = 2)
    private BigDecimal baseAmount;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal convenienceFee = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalAmount;

    private LocalDateTime bookedAt;

    private LocalDateTime cancelledAt;

    private String cancellationReason;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookedSeat> bookedSeats = new ArrayList<>();

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;
}
```

**File: `domain/booking/BookedSeat.java`**
```java
package com.razkart.cinehub.domain.booking;

import com.razkart.cinehub.common.enums.SeatStatus;
import com.razkart.cinehub.domain.theater.Seat;
import com.razkart.cinehub.domain.theater.Show;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booked_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatStatus status = SeatStatus.LOCKED;

    private LocalDateTime lockExpiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**File: `domain/booking/Payment.java`**
```java
package com.razkart.cinehub.domain.booking;

import com.razkart.cinehub.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 50)
    private String paymentMethod;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    private String paymentGateway;

    @Column(columnDefinition = "TEXT")
    private String gatewayResponse;

    private LocalDateTime paidAt;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundAmount = BigDecimal.ZERO;

    private LocalDateTime refundedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

### Step 8: Create Exception Classes

**File: `common/exception/ResourceNotFoundException.java`**
```java
package com.razkart.cinehub.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s not found with id: %d", resource, id));
    }

    public ResourceNotFoundException(String resource, String field, String value) {
        super(String.format("%s not found with %s: %s", resource, field, value));
    }
}
```

**File: `common/exception/SeatAlreadyBookedException.java`**
```java
package com.razkart.cinehub.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SeatAlreadyBookedException extends RuntimeException {

    public SeatAlreadyBookedException(String message) {
        super(message);
    }
}
```

**File: `common/exception/BookingExpiredException.java`**
```java
package com.razkart.cinehub.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class BookingExpiredException extends RuntimeException {

    public BookingExpiredException(String message) {
        super(message);
    }
}
```

**File: `common/exception/PaymentFailedException.java`**
```java
package com.razkart.cinehub.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
        super(message);
    }
}
```

**File: `common/exception/GlobalExceptionHandler.java`**
```java
package com.razkart.cinehub.common.exception;

import com.razkart.cinehub.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ApiResponse<Void>> handleSeatBooked(SeatAlreadyBookedException ex) {
        log.warn("Seat booking conflict: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage(), "SEAT_UNAVAILABLE"));
    }

    @ExceptionHandler(BookingExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleBookingExpired(BookingExpiredException ex) {
        log.warn("Booking expired: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.GONE)
            .body(ApiResponse.error(ex.getMessage(), "BOOKING_EXPIRED"));
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentFailed(PaymentFailedException ex) {
        log.error("Payment failed: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.PAYMENT_REQUIRED)
            .body(ApiResponse.error(ex.getMessage(), "PAYMENT_FAILED"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Invalid email or password", "INVALID_CREDENTIALS"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Access denied", "ACCESS_DENIED"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.validationError(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage(), "VALIDATION_ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An unexpected error occurred", "INTERNAL_ERROR"));
    }
}
```

---

### Step 9: Create DTOs

**File: `dto/response/ApiResponse.java`**
```java
package com.razkart.cinehub.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private Map<String, String> errors;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Success")
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

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .build();
    }

    public static ApiResponse<Map<String, String>> validationError(Map<String, String> errors) {
        return ApiResponse.<Map<String, String>>builder()
            .success(false)
            .message("Validation failed")
            .errorCode("VALIDATION_ERROR")
            .errors(errors)
            .build();
    }
}
```

**File: `dto/response/PagedResponse.java`**
```java
package com.razkart.cinehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T> PagedResponse<T> from(Page<T> page) {
        return PagedResponse.<T>builder()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
```

---

### Step 10: Create Configuration Classes

**File: `config/AuditConfig.java`**
```java
package com.razkart.cinehub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }
            return Optional.of(auth.getName());
        };
    }
}
```

**File: `config/CorsConfig.java`**
```java
package com.razkart.cinehub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

**File: `config/OpenApiConfig.java`**
```java
package com.razkart.cinehub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("CineHub API")
                .version("1.0")
                .description("Movie Ticketing Platform API Documentation")
                .contact(new Contact()
                    .name("CineHub Team")
                    .email("support@cinehub.com"))
                .license(new License()
                    .name("MIT License")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

---

### Step 11: Create Repositories

**File: `repository/UserRepository.java`**
```java
package com.razkart.cinehub.repository;

import com.razkart.cinehub.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
```

**File: `repository/RoleRepository.java`**
```java
package com.razkart.cinehub.repository;

import com.razkart.cinehub.domain.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
```

**File: `repository/MovieRepository.java`**
```java
package com.razkart.cinehub.repository;

import com.razkart.cinehub.domain.movie.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Page<Movie> findByActiveTrue(Pageable pageable);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    @Query("""
        SELECT DISTINCT m FROM Movie m
        JOIN Show s ON s.movie = m
        JOIN Screen sc ON s.screen = sc
        JOIN Theater t ON sc.theater = t
        WHERE t.city.id = :cityId
        AND s.showDate >= :date
        AND s.active = true
        AND m.active = true
        """)
    List<Movie> findNowShowingInCity(@Param("cityId") Long cityId,
                                      @Param("date") LocalDate date);

    @Query("SELECT m FROM Movie m WHERE m.releaseDate > :today AND m.active = true")
    List<Movie> findComingSoon(@Param("today") LocalDate today);
}
```

**File: `repository/ShowRepository.java`**
```java
package com.razkart.cinehub.repository;

import com.razkart.cinehub.domain.theater.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("""
        SELECT s FROM Show s
        JOIN FETCH s.screen sc
        JOIN FETCH sc.theater t
        WHERE s.movie.id = :movieId
        AND t.city.id = :cityId
        AND s.showDate = :date
        AND s.active = true
        ORDER BY t.name, s.startTime
        """)
    List<Show> findShowsForMovieInCity(@Param("movieId") Long movieId,
                                        @Param("cityId") Long cityId,
                                        @Param("date") LocalDate date);

    @Query("""
        SELECT s FROM Show s
        WHERE s.screen.theater.id = :theaterId
        AND s.showDate = :date
        AND s.active = true
        ORDER BY s.movie.title, s.startTime
        """)
    List<Show> findShowsInTheater(@Param("theaterId") Long theaterId,
                                   @Param("date") LocalDate date);
}
```

**File: `repository/BookingRepository.java`**
```java
package com.razkart.cinehub.repository;

import com.razkart.cinehub.domain.booking.Booking;
import com.razkart.cinehub.common.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Booking> findByShowIdAndStatus(Long showId, BookingStatus status, Pageable pageable);
}
```

**File: `repository/BookedSeatRepository.java`**
```java
package com.razkart.cinehub.repository;

import com.razkart.cinehub.domain.booking.BookedSeat;
import com.razkart.cinehub.common.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {

    @Query("""
        SELECT bs FROM BookedSeat bs
        WHERE bs.show.id = :showId
        AND (bs.status = 'BOOKED'
             OR (bs.status = 'LOCKED' AND bs.lockExpiresAt > :now))
        """)
    List<BookedSeat> findUnavailableSeatsForShow(@Param("showId") Long showId,
                                                  @Param("now") LocalDateTime now);

    List<BookedSeat> findByStatusAndLockExpiresAtBefore(SeatStatus status,
                                                         LocalDateTime expiry);

    @Modifying
    @Query("""
        UPDATE BookedSeat bs
        SET bs.status = 'RELEASED'
        WHERE bs.status = 'LOCKED'
        AND bs.lockExpiresAt < :now
        """)
    int releaseExpiredLocks(@Param("now") LocalDateTime now);
}
```

---

## Seat Locking Algorithm (Critical)

```
┌──────────────────────────────────────────────────────────────────┐
│                    SEAT LOCKING FLOW                              │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  1. User selects seats (e.g., A1, A2, A3)                        │
│                    │                                              │
│                    ▼                                              │
│  2. Check availability:                                          │
│     - Query booked_seats WHERE show_id = X                       │
│     - Filter: status='BOOKED' OR                                 │
│              (status='LOCKED' AND lock_expires > NOW)            │
│                    │                                              │
│         ┌─────────┴─────────┐                                    │
│         │                   │                                    │
│    AVAILABLE            CONFLICT                                 │
│         │                   │                                    │
│         ▼                   ▼                                    │
│  3. Lock seats         Return error                              │
│     - Create Booking (PENDING)                                   │
│     - Create BookedSeat records                                  │
│       - status = LOCKED                                          │
│       - lock_expires = NOW + 5 minutes                           │
│                    │                                              │
│                    ▼                                              │
│  4. Return booking_id + expiry time                              │
│     (Frontend shows countdown)                                   │
│                    │                                              │
│         ┌─────────┴─────────┐                                    │
│         │                   │                                    │
│   PAYMENT OK         PAYMENT FAIL/TIMEOUT                        │
│         │                   │                                    │
│         ▼                   ▼                                    │
│  5. Confirm:           Release:                                  │
│     - booking.status   - booking.status = EXPIRED                │
│       = CONFIRMED      - seats.status = RELEASED                 │
│     - seats.status                                               │
│       = BOOKED                                                   │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## Best Practices Checklist

- [ ] Always use `@Transactional` on service methods that modify data
- [ ] Use `FetchType.LAZY` for relationships to avoid N+1 queries
- [ ] Create custom queries with `JOIN FETCH` when you need related data
- [ ] Use DTOs instead of exposing entities in API responses
- [ ] Validate all input using `@Valid` and Jakarta Validation annotations
- [ ] Handle exceptions globally with `@RestControllerAdvice`
- [ ] Use `@Version` for optimistic locking on frequently updated entities
- [ ] Log important operations and errors
- [ ] Write unit tests for services, integration tests for repositories
- [ ] Use pagination for list endpoints (`Pageable`)

---

## Common Commands

```bash
# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test

# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/cine-hub-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Generate API docs
# Access: http://localhost:8080/api/swagger-ui.html
```
