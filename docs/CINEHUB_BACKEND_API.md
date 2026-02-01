# CineHub Backend API Documentation

> **Version:** 1.0.0
> **Base URL:** `http://localhost:8080/api`
> **Swagger UI:** `http://localhost:8080/api/swagger-ui.html`

---

## Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Authentication](#authentication)
4. [API Endpoints](#api-endpoints)
5. [Data Models](#data-models)
6. [Error Handling](#error-handling)
7. [Business Flows](#business-flows)

---

## Overview

CineHub is a movie ticketing platform similar to BookMyShow. This document provides the complete API reference for frontend integration.

### Key Features
- User authentication with JWT
- Movie/Event browsing and search
- Theater and screen management
- Real-time seat selection with locking mechanism
- Secure booking and payment flow
- Ticket generation with QR codes

### User Roles
| Role | Description |
|------|-------------|
| `CUSTOMER` | Browse, book tickets, manage profile |
| `THEATER_OWNER` | Manage theaters, screens, shows |
| `ADMIN` | Full system access |

---

## Technology Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 4.0.2 |
| Language | Java 21 |
| Database | MySQL 8.x |
| Cache | Redis 7.x |
| Queue | RabbitMQ 3.x |
| Auth | JWT (HS256) |
| API Docs | OpenAPI 3.0 |

---

## Authentication

### JWT Token
- **Algorithm:** HS256
- **Expiration:** 24 hours
- **Header Format:** `Authorization: Bearer <token>`

### Public Endpoints (No Auth Required)
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/events/*`
- `GET /api/v1/venues/*`
- `GET /api/v1/cities`
- `GET /api/v1/shows/*`
- `GET /api/v1/search`

### Protected Endpoints (Auth Required)
- All `/api/v1/bookings/*`
- All `/api/v1/payments/*`
- `GET /api/v1/users/me`
- `PATCH /api/v1/users/me`

---

## API Endpoints

### Authentication APIs

#### Register User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "fullName": "John Doe",
  "password": "securePassword123",
  "phone": "+919876543210"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "phone": "+919876543210",
    "role": "CUSTOMER",
    "status": "ACTIVE",
    "emailVerified": false,
    "phoneVerified": false,
    "createdAt": "2024-01-15T10:30:00"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "user@example.com",
      "fullName": "John Doe",
      "role": "CUSTOMER"
    }
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

### User APIs

#### Get Current User Profile
```http
GET /api/v1/users/me
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "phone": "+919876543210",
    "profileImage": "https://...",
    "role": "CUSTOMER",
    "status": "ACTIVE",
    "emailVerified": true,
    "phoneVerified": false,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### Update Profile
```http
PATCH /api/v1/users/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "John Smith",
  "phone": "+919876543211"
}
```

---

### Event/Movie APIs

#### List Events (Paginated)
```http
GET /api/v1/events?page=0&size=20&category=MOVIE&status=NOW_SHOWING
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Inception",
        "description": "A mind-bending thriller...",
        "category": "MOVIE",
        "language": "English",
        "durationMinutes": 148,
        "rating": "UA",
        "genre": "Sci-Fi, Thriller",
        "posterUrl": "https://...",
        "bannerUrl": "https://...",
        "trailerUrl": "https://youtube.com/...",
        "releaseDate": "2024-01-15",
        "status": "NOW_SHOWING",
        "avgRating": 4.5,
        "totalReviews": 1250,
        "createdAt": "2024-01-10T10:00:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 50,
    "totalPages": 3,
    "last": false
  }
}
```

#### Get Event Details
```http
GET /api/v1/events/{id}
```

#### Get Now Showing Events
```http
GET /api/v1/events/now-showing?cityId=1
```

#### Get Coming Soon Events
```http
GET /api/v1/events/coming-soon
```

#### Get Events by Category
```http
GET /api/v1/events/category/{category}
```
**Categories:** `MOVIE`, `CONCERT`, `SPORT`, `PLAY`, `COMEDY`, `OTHER`

---

### Venue/Theater APIs

#### List Cities
```http
GET /api/v1/cities
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Mumbai",
      "state": "Maharashtra",
      "isActive": true
    },
    {
      "id": 2,
      "name": "Delhi",
      "state": "Delhi",
      "isActive": true
    }
  ]
}
```

#### Get Venues in City
```http
GET /api/v1/cities/{cityId}/venues
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "PVR Phoenix",
      "city": {
        "id": 1,
        "name": "Mumbai"
      },
      "address": "Phoenix Mall, Lower Parel",
      "landmark": "Near Lower Parel Station",
      "latitude": 19.0176,
      "longitude": 72.8314,
      "contactPhone": "+912212345678",
      "contactEmail": "phoenix@pvr.com",
      "facilities": {
        "parking": true,
        "food_court": true,
        "wheelchair_access": true
      },
      "status": "ACTIVE",
      "screens": [
        {
          "id": 1,
          "name": "Screen 1",
          "screenType": "IMAX",
          "totalSeats": 250
        }
      ]
    }
  ]
}
```

#### Get Venue Details
```http
GET /api/v1/venues/{id}
```

---

### Show/Showtime APIs

#### Get Shows for Event in City
```http
GET /api/v1/shows/event/{eventId}?cityId=1&date=2024-01-15
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "event": {
        "id": 1,
        "title": "Inception"
      },
      "screen": {
        "id": 1,
        "name": "Screen 1",
        "screenType": "IMAX",
        "venue": {
          "id": 1,
          "name": "PVR Phoenix"
        }
      },
      "showDate": "2024-01-15",
      "startTime": "14:30:00",
      "endTime": "17:00:00",
      "status": "SCHEDULED",
      "pricing": {
        "REGULAR": 250.00,
        "PREMIUM": 350.00,
        "RECLINER": 500.00,
        "VIP": 800.00
      }
    }
  ]
}
```

#### Get Show Details
```http
GET /api/v1/shows/{id}
```

#### Get Seat Availability for Show
```http
GET /api/v1/bookings/shows/{showId}/seats
```

**Response:**
```json
{
  "success": true,
  "data": {
    "showId": 1,
    "seats": [
      {
        "id": 1,
        "rowName": "A",
        "seatNumber": 1,
        "seatLabel": "A1",
        "seatType": "REGULAR",
        "xPosition": 0,
        "yPosition": 0,
        "isAvailable": true
      },
      {
        "id": 2,
        "rowName": "A",
        "seatNumber": 2,
        "seatLabel": "A2",
        "seatType": "REGULAR",
        "xPosition": 1,
        "yPosition": 0,
        "isAvailable": true
      }
    ],
    "unavailableSeatIds": [5, 6, 7, 15, 16],
    "pricing": {
      "REGULAR": 250.00,
      "PREMIUM": 350.00,
      "RECLINER": 500.00,
      "VIP": 800.00
    }
  }
}
```

---

### Booking APIs (CRITICAL)

#### Initiate Booking (Lock Seats)
```http
POST /api/v1/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
  "showId": 1,
  "seatIds": [1, 2, 3]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Booking initiated. Complete payment within 10 minutes.",
  "data": {
    "id": 1,
    "bookingNumber": "CH1705312200001234",
    "status": "PENDING",
    "totalAmount": 750.00,
    "convenienceFee": 37.50,
    "taxAmount": 141.75,
    "discountAmount": 0.00,
    "finalAmount": 929.25,
    "bookedSeats": [
      {
        "seatId": 1,
        "seatLabel": "A1",
        "seatType": "REGULAR",
        "price": 250.00
      },
      {
        "seatId": 2,
        "seatLabel": "A2",
        "seatType": "REGULAR",
        "price": 250.00
      },
      {
        "seatId": 3,
        "seatLabel": "A3",
        "seatType": "REGULAR",
        "price": 250.00
      }
    ],
    "expiresAt": "2024-01-15T10:40:00",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### Confirm Booking (After Payment)
```http
POST /api/v1/bookings/{bookingId}/confirm
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 929.25
}
```

**Response:**
```json
{
  "success": true,
  "message": "Booking confirmed successfully",
  "data": {
    "id": 1,
    "bookingNumber": "CH1705312200001234",
    "status": "CONFIRMED",
    "paymentStatus": "SUCCESS",
    "totalAmount": 750.00,
    "convenienceFee": 37.50,
    "taxAmount": 141.75,
    "finalAmount": 929.25,
    "bookedSeats": [...],
    "tickets": [
      {
        "id": 1,
        "ticketNumber": "TKT1705312200001001",
        "seatLabel": "A1",
        "qrCode": "Q0gxNzA1MzEyMjAwMDAxMjM0fEEx",
        "status": "VALID"
      }
    ],
    "bookedAt": "2024-01-15T10:35:00"
  }
}
```

#### Cancel Booking
```http
POST /api/v1/bookings/{bookingId}/cancel
Authorization: Bearer <token>
Content-Type: application/json

{
  "reason": "Changed plans"
}
```

#### Get Booking Details
```http
GET /api/v1/bookings/{bookingId}
Authorization: Bearer <token>
```

#### Get Booking by Number
```http
GET /api/v1/bookings/number/{bookingNumber}
Authorization: Bearer <token>
```

#### Get User's Bookings
```http
GET /api/v1/bookings?page=0&size=10
Authorization: Bearer <token>
```

---

### Payment APIs

#### Initiate Payment
```http
POST /api/v1/payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "bookingId": 1,
  "amount": 929.25,
  "paymentMethod": "UPI",
  "paymentGateway": "RAZORPAY"
}
```

**Payment Methods:** `UPI`, `CREDIT_CARD`, `DEBIT_CARD`, `NETBANKING`, `WALLET`

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "bookingId": 1,
    "amount": 929.25,
    "paymentMethod": "UPI",
    "status": "INITIATED",
    "gatewayOrderId": "order_xyz123",
    "initiatedAt": "2024-01-15T10:32:00"
  }
}
```

#### Payment Callback (Gateway Webhook)
```http
POST /api/v1/payments/callback
Content-Type: application/json

{
  "gatewayOrderId": "order_xyz123",
  "gatewayPaymentId": "pay_abc789",
  "gatewaySignature": "...",
  "status": "SUCCESS"
}
```

#### Get Payment Details
```http
GET /api/v1/payments/{paymentId}
Authorization: Bearer <token>
```

---

### Search APIs

#### Global Search
```http
GET /api/v1/search?query=inception&cityId=1&category=MOVIE
```

**Response:**
```json
{
  "success": true,
  "data": {
    "events": [...],
    "venues": [...],
    "shows": [...]
  }
}
```

---

## Data Models

### Enums

#### EventCategory
```
MOVIE, CONCERT, SPORT, PLAY, COMEDY, OTHER
```

#### EventStatus
```
COMING_SOON, NOW_SHOWING, ENDED
```

#### MovieRating (Certificate)
```
U (Universal), UA (Parental Guidance), A (Adults), S (Special)
```

#### ScreenType
```
REGULAR, IMAX, 4DX, DOLBY_ATMOS, PREMIUM, GOLD
```

#### SeatType
```
REGULAR, PREMIUM, RECLINER, VIP, WHEELCHAIR
```

#### BookingStatus
```
PENDING     - Seats locked, awaiting payment (10 min timeout)
CONFIRMED   - Payment successful, tickets generated
CANCELLED   - Cancelled by user or system
EXPIRED     - Lock expired without payment
```

#### PaymentStatus
```
PENDING, SUCCESS, FAILED, REFUNDED, PARTIAL_REFUND
```

#### PaymentMethod
```
UPI, CREDIT_CARD, DEBIT_CARD, NETBANKING, WALLET
```

---

## Error Handling

### Standard Error Response
```json
{
  "success": false,
  "message": "Error description",
  "errorCode": "ERROR_CODE",
  "errors": {
    "field": "Validation error message"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `RESOURCE_NOT_FOUND` | 404 | Resource doesn't exist |
| `SEAT_UNAVAILABLE` | 409 | Seats already booked/locked |
| `BOOKING_EXPIRED` | 410 | Booking lock expired |
| `PAYMENT_FAILED` | 402 | Payment processing failed |
| `VALIDATION_ERROR` | 400 | Invalid request data |
| `INVALID_CREDENTIALS` | 401 | Wrong email/password |
| `ACCESS_DENIED` | 403 | Insufficient permissions |
| `INTERNAL_ERROR` | 500 | Server error |

---

## Business Flows

### Movie Booking Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                        BOOKING FLOW                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  1. Browse Movies                                                    │
│     GET /api/v1/events/now-showing?cityId=1                         │
│                          │                                           │
│                          ▼                                           │
│  2. Select Movie → View Showtimes                                   │
│     GET /api/v1/shows/event/{eventId}?cityId=1&date=2024-01-15     │
│                          │                                           │
│                          ▼                                           │
│  3. Select Show → View Seat Layout                                  │
│     GET /api/v1/bookings/shows/{showId}/seats                       │
│                          │                                           │
│                          ▼                                           │
│  4. Select Seats → Lock Seats (10 min)                              │
│     POST /api/v1/bookings                                           │
│     Body: { showId, seatIds: [1,2,3] }                              │
│                          │                                           │
│                          ▼                                           │
│  5. Start Payment Timer (Frontend: 10 min countdown)                │
│     - Show booking summary                                          │
│     - Collect payment details                                       │
│                          │                                           │
│              ┌───────────┴───────────┐                              │
│              │                       │                               │
│         SUCCESS                   TIMEOUT                            │
│              │                       │                               │
│              ▼                       ▼                               │
│  6a. Confirm Booking          6b. Booking Expires                   │
│      POST /bookings/{id}/confirm    - Seats released                │
│      Body: { amount }               - Status → EXPIRED              │
│              │                       │                               │
│              ▼                       ▼                               │
│  7. Get Tickets              Redirect to seat selection             │
│     - QR codes generated                                            │
│     - Email/SMS sent                                                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Seat Locking Mechanism

**Why Seat Locking?**
- Prevents double-booking when multiple users select same seats
- Uses Redis for distributed locking (10-minute TTL)
- Database-level UNIQUE constraint as fallback

**Lock Flow:**
1. User selects seats
2. Backend attempts Redis SETNX for each seat
3. If all succeed → Create PENDING booking
4. If any fail → Release acquired locks, return error
5. Background job releases expired locks every minute

### Pricing Calculation

```
Ticket Amount    = Sum of seat prices based on seat type
Convenience Fee  = 5% of Ticket Amount
Subtotal         = Ticket Amount + Convenience Fee
Tax (GST)        = 18% of Subtotal
Final Amount     = Subtotal + Tax - Discount
```

---

## Rate Limits

| Endpoint Type | Limit |
|---------------|-------|
| Authentication | 5 requests/minute |
| Booking Creation | 3 requests/minute |
| General API | 100 requests/minute |

---

## Webhook Events

CineHub sends webhooks for these events:

| Event | Description |
|-------|-------------|
| `booking.confirmed` | Booking successfully confirmed |
| `booking.cancelled` | Booking cancelled |
| `booking.expired` | Booking lock expired |
| `payment.success` | Payment processed successfully |
| `payment.failed` | Payment failed |
| `refund.processed` | Refund completed |

---

## Quick Reference

### Base URLs
- **Development:** `http://localhost:8080/api`
- **Production:** `https://api.cinehub.com/api`

### Common Headers
```http
Authorization: Bearer <jwt_token>
Content-Type: application/json
Accept: application/json
```

### Booking Constraints
- Maximum 10 seats per booking
- Seat lock timeout: 10 minutes
- Cancellation allowed: 2 hours before show

---

*Document Version: 1.0.0 | Last Updated: 2024-01-15*
