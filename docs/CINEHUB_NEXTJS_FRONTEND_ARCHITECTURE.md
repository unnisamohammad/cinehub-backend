# CineHub Next.js Frontend Architecture

> **For use with Claude Code in VS Code**
> **Version:** 1.0.0
> **Target:** Next.js 15+ with App Router, TypeScript, Redux Toolkit

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [High-Level Design (HLD)](#high-level-design-hld)
3. [Low-Level Design (LLD)](#low-level-design-lld)
4. [Implementation Guide](#implementation-guide)
5. [API Integration](#api-integration)
6. [State Management](#state-management)
7. [Component Library](#component-library)
8. [Edge Cases & Solutions](#edge-cases--solutions)

---

## Executive Summary

### Project Overview
CineHub is a modern movie ticketing platform. This document provides the complete frontend architecture using Next.js 15 with App Router.

### Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| Framework | Next.js 15+ (App Router) | SSR, SSG, Server Components |
| Language | TypeScript 5.x | Type safety |
| Styling | Tailwind CSS 3.x | Utility-first CSS |
| State | Redux Toolkit + RTK Query | Global state + API caching |
| Forms | React Hook Form + Zod | Form handling + validation |
| UI Components | Radix UI + shadcn/ui | Accessible components |
| Animation | Framer Motion | Smooth transitions |
| Icons | Lucide React | Modern icon set |
| Date | date-fns | Date manipulation |
| Auth | NextAuth.js v5 | Authentication |

### Design Principles

1. **Server-First Approach** - Leverage Next.js Server Components for data fetching
2. **Progressive Enhancement** - Core functionality works without JS
3. **Type Safety** - Strict TypeScript everywhere
4. **Atomic Design** - Reusable component hierarchy
5. **KISS Principle** - Keep implementations simple
6. **Separation of Concerns** - Clear boundaries between layers
7. **DRY** - Shared utilities and hooks
8. **SOLID** - Single responsibility, Open/closed, etc.

---

## High-Level Design (HLD)

### System Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CINEHUB FRONTEND                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                        PRESENTATION LAYER                           │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │ │
│  │  │  Pages   │  │  Layouts │  │Components│  │    UI Library    │  │ │
│  │  │ (Routes) │  │(Wrappers)│  │(Features)│  │ (shadcn/ui)      │  │ │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                      APPLICATION LAYER                              │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │ │
│  │  │  Hooks   │  │ Context  │  │   Redux  │  │  Server Actions  │  │ │
│  │  │ (Custom) │  │(Providers)│  │  Store   │  │  (Mutations)     │  │ │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                         DATA LAYER                                  │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │ │
│  │  │RTK Query │  │   API    │  │   Types  │  │    Utilities     │  │ │
│  │  │(Caching) │  │ Services │  │  (Zod)   │  │   (Helpers)      │  │ │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
└────────────────────────────────────│─────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                       SPRING BOOT BACKEND API                            │
│                    http://localhost:8080/api/v1                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Application Routes

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           ROUTE STRUCTURE                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  /                          → Home (Now Showing + Coming Soon)           │
│  /movies                    → All Movies List                            │
│  /movies/[id]               → Movie Details + Shows                      │
│  /movies/[id]/book/[showId] → Seat Selection                             │
│  /checkout/[bookingId]      → Payment + Confirmation                     │
│                                                                          │
│  /theaters                  → Theater List by City                       │
│  /theaters/[id]             → Theater Details + Shows                    │
│                                                                          │
│  /search                    → Search Results                             │
│                                                                          │
│  /auth/login                → Login Page                                 │
│  /auth/register             → Registration Page                          │
│  /auth/forgot-password      → Password Reset                             │
│                                                                          │
│  /profile                   → User Profile (Protected)                   │
│  /profile/bookings          → Booking History (Protected)                │
│  /profile/bookings/[id]     → Booking Details (Protected)                │
│                                                                          │
│  /admin/*                   → Admin Dashboard (Role Protected)           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### User Flows

#### 1. Movie Booking Flow (Critical Path)

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  Browse  │ →  │  Select  │ →  │  Select  │ →  │  Select  │ →  │ Payment  │
│  Movies  │    │  Movie   │    │   Show   │    │  Seats   │    │ Checkout │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
     │               │               │               │               │
     ▼               ▼               ▼               ▼               ▼
  /movies       /movies/[id]   /movies/[id]  /movies/[id]/   /checkout/
                               ?date=...     book/[showId]   [bookingId]
                                                   │
                                                   ▼
                                            ┌─────────────┐
                                            │ 10 MIN LOCK │
                                            │  COUNTDOWN  │
                                            └─────────────┘
```

#### 2. Authentication Flow

```
┌──────────────────────────────────────────────────────────────────┐
│                    AUTHENTICATION FLOW                            │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Unauthenticated User                                            │
│           │                                                       │
│           ▼                                                       │
│  ┌─────────────────┐                                             │
│  │ Try to Book     │──────► Redirect to /auth/login              │
│  │ (Protected)     │        ?redirect=/movies/1/book/5           │
│  └─────────────────┘                                             │
│           │                                                       │
│           ▼                                                       │
│  ┌─────────────────┐      ┌─────────────────┐                   │
│  │ Login           │  OR  │ Register        │                   │
│  │ /auth/login     │      │ /auth/register  │                   │
│  └────────┬────────┘      └────────┬────────┘                   │
│           │                        │                             │
│           └──────────┬─────────────┘                             │
│                      ▼                                           │
│           ┌─────────────────┐                                    │
│           │ JWT Token       │                                    │
│           │ Stored in       │                                    │
│           │ HTTP-only Cookie│                                    │
│           └────────┬────────┘                                    │
│                    ▼                                             │
│           Redirect to original destination                       │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## Low-Level Design (LLD)

### Project Structure

```
cinehub-frontend/
├── .env.local                    # Environment variables
├── .env.example                  # Example env file
├── next.config.ts                # Next.js configuration
├── tailwind.config.ts            # Tailwind configuration
├── tsconfig.json                 # TypeScript configuration
├── package.json
│
├── public/
│   ├── images/
│   │   ├── logo.svg
│   │   ├── placeholder-movie.jpg
│   │   └── placeholder-avatar.jpg
│   └── icons/
│       └── favicon.ico
│
├── src/
│   │
│   ├── app/                      # Next.js App Router
│   │   ├── layout.tsx            # Root layout
│   │   ├── page.tsx              # Home page
│   │   ├── loading.tsx           # Global loading
│   │   ├── error.tsx             # Global error
│   │   ├── not-found.tsx         # 404 page
│   │   │
│   │   ├── (auth)/               # Auth route group
│   │   │   ├── layout.tsx        # Auth layout (centered)
│   │   │   ├── login/
│   │   │   │   └── page.tsx
│   │   │   ├── register/
│   │   │   │   └── page.tsx
│   │   │   └── forgot-password/
│   │   │       └── page.tsx
│   │   │
│   │   ├── (main)/               # Main route group
│   │   │   ├── layout.tsx        # Main layout (header/footer)
│   │   │   │
│   │   │   ├── movies/
│   │   │   │   ├── page.tsx      # Movies list
│   │   │   │   ├── loading.tsx
│   │   │   │   └── [id]/
│   │   │   │       ├── page.tsx  # Movie details
│   │   │   │       ├── loading.tsx
│   │   │   │       └── book/
│   │   │   │           └── [showId]/
│   │   │   │               ├── page.tsx  # Seat selection
│   │   │   │               └── loading.tsx
│   │   │   │
│   │   │   ├── theaters/
│   │   │   │   ├── page.tsx
│   │   │   │   └── [id]/
│   │   │   │       └── page.tsx
│   │   │   │
│   │   │   ├── search/
│   │   │   │   └── page.tsx
│   │   │   │
│   │   │   └── checkout/
│   │   │       └── [bookingId]/
│   │   │           ├── page.tsx  # Payment page
│   │   │           ├── success/
│   │   │           │   └── page.tsx
│   │   │           └── failed/
│   │   │               └── page.tsx
│   │   │
│   │   ├── (protected)/          # Protected route group
│   │   │   ├── layout.tsx        # Auth check layout
│   │   │   │
│   │   │   └── profile/
│   │   │       ├── page.tsx      # User profile
│   │   │       ├── bookings/
│   │   │       │   ├── page.tsx  # Booking history
│   │   │       │   └── [id]/
│   │   │       │       └── page.tsx  # Booking details
│   │   │       └── settings/
│   │   │           └── page.tsx
│   │   │
│   │   └── api/                  # API routes (if needed)
│   │       └── auth/
│   │           └── [...nextauth]/
│   │               └── route.ts
│   │
│   ├── components/               # React Components
│   │   │
│   │   ├── ui/                   # shadcn/ui components
│   │   │   ├── button.tsx
│   │   │   ├── card.tsx
│   │   │   ├── dialog.tsx
│   │   │   ├── dropdown-menu.tsx
│   │   │   ├── input.tsx
│   │   │   ├── label.tsx
│   │   │   ├── select.tsx
│   │   │   ├── skeleton.tsx
│   │   │   ├── toast.tsx
│   │   │   ├── tabs.tsx
│   │   │   └── badge.tsx
│   │   │
│   │   ├── layout/               # Layout components
│   │   │   ├── header.tsx
│   │   │   ├── footer.tsx
│   │   │   ├── sidebar.tsx
│   │   │   ├── mobile-nav.tsx
│   │   │   └── city-selector.tsx
│   │   │
│   │   ├── movies/               # Movie-related components
│   │   │   ├── movie-card.tsx
│   │   │   ├── movie-grid.tsx
│   │   │   ├── movie-carousel.tsx
│   │   │   ├── movie-details.tsx
│   │   │   ├── movie-filters.tsx
│   │   │   └── movie-search.tsx
│   │   │
│   │   ├── shows/                # Show-related components
│   │   │   ├── show-times.tsx
│   │   │   ├── show-card.tsx
│   │   │   ├── date-selector.tsx
│   │   │   └── theater-shows.tsx
│   │   │
│   │   ├── booking/              # Booking components (CRITICAL)
│   │   │   ├── seat-layout.tsx
│   │   │   ├── seat.tsx
│   │   │   ├── seat-legend.tsx
│   │   │   ├── booking-summary.tsx
│   │   │   ├── booking-timer.tsx
│   │   │   ├── price-breakdown.tsx
│   │   │   └── ticket-card.tsx
│   │   │
│   │   ├── payment/              # Payment components
│   │   │   ├── payment-form.tsx
│   │   │   ├── payment-methods.tsx
│   │   │   └── payment-status.tsx
│   │   │
│   │   ├── auth/                 # Auth components
│   │   │   ├── login-form.tsx
│   │   │   ├── register-form.tsx
│   │   │   ├── auth-guard.tsx
│   │   │   └── user-menu.tsx
│   │   │
│   │   └── shared/               # Shared components
│   │       ├── loading-spinner.tsx
│   │       ├── empty-state.tsx
│   │       ├── error-boundary.tsx
│   │       ├── rating-stars.tsx
│   │       ├── price-tag.tsx
│   │       └── qr-code.tsx
│   │
│   ├── lib/                      # Core utilities
│   │   ├── api/                  # API layer
│   │   │   ├── client.ts         # Axios instance
│   │   │   ├── endpoints.ts      # API endpoints
│   │   │   └── types.ts          # API response types
│   │   │
│   │   ├── utils/                # Utility functions
│   │   │   ├── cn.ts             # Classname utility
│   │   │   ├── format.ts         # Formatters (date, currency)
│   │   │   ├── validation.ts     # Validation schemas
│   │   │   └── constants.ts      # App constants
│   │   │
│   │   └── auth/                 # Auth utilities
│   │       ├── session.ts
│   │       └── middleware.ts
│   │
│   ├── hooks/                    # Custom React hooks
│   │   ├── use-auth.ts
│   │   ├── use-booking.ts
│   │   ├── use-countdown.ts
│   │   ├── use-city.ts
│   │   ├── use-debounce.ts
│   │   ├── use-local-storage.ts
│   │   └── use-media-query.ts
│   │
│   ├── store/                    # Redux store
│   │   ├── index.ts              # Store configuration
│   │   ├── provider.tsx          # Redux provider
│   │   │
│   │   ├── slices/               # Redux slices
│   │   │   ├── auth-slice.ts
│   │   │   ├── booking-slice.ts
│   │   │   ├── city-slice.ts
│   │   │   └── ui-slice.ts
│   │   │
│   │   └── api/                  # RTK Query API
│   │       ├── base-api.ts       # Base API configuration
│   │       ├── auth-api.ts
│   │       ├── movies-api.ts
│   │       ├── venues-api.ts
│   │       ├── shows-api.ts
│   │       ├── bookings-api.ts
│   │       └── payments-api.ts
│   │
│   ├── types/                    # TypeScript types
│   │   ├── index.ts              # Export all types
│   │   ├── api.ts                # API response types
│   │   ├── auth.ts               # Auth types
│   │   ├── movie.ts              # Movie types
│   │   ├── venue.ts              # Venue types
│   │   ├── show.ts               # Show types
│   │   ├── booking.ts            # Booking types
│   │   └── payment.ts            # Payment types
│   │
│   ├── styles/                   # Global styles
│   │   └── globals.css           # Tailwind imports + custom CSS
│   │
│   └── middleware.ts             # Next.js middleware (auth)
│
└── tests/                        # Test files
    ├── components/
    └── hooks/
```

### Core Types (TypeScript)

```typescript
// src/types/api.ts
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode?: string;
  errors?: Record<string, string>;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// src/types/auth.ts
export interface User {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  profileImage?: string;
  role: 'CUSTOMER' | 'THEATER_OWNER' | 'ADMIN';
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
  emailVerified: boolean;
  phoneVerified: boolean;
  createdAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: User;
}

export interface RegisterRequest {
  email: string;
  fullName: string;
  password: string;
  phone?: string;
}

// src/types/movie.ts
export type EventCategory = 'MOVIE' | 'CONCERT' | 'SPORT' | 'PLAY' | 'COMEDY' | 'OTHER';
export type EventStatus = 'COMING_SOON' | 'NOW_SHOWING' | 'ENDED';
export type Rating = 'U' | 'UA' | 'A' | 'S';

export interface Movie {
  id: number;
  title: string;
  description: string;
  category: EventCategory;
  language: string;
  durationMinutes: number;
  rating: Rating;
  genre: string;
  posterUrl: string;
  bannerUrl?: string;
  trailerUrl?: string;
  releaseDate: string;
  status: EventStatus;
  avgRating: number;
  totalReviews: number;
  createdAt: string;
}

// src/types/venue.ts
export interface City {
  id: number;
  name: string;
  state: string;
  isActive: boolean;
}

export type ScreenType = 'REGULAR' | 'IMAX' | '4DX' | 'DOLBY_ATMOS' | 'PREMIUM' | 'GOLD';

export interface Screen {
  id: number;
  name: string;
  screenType: ScreenType;
  totalSeats: number;
}

export interface Venue {
  id: number;
  name: string;
  city: City;
  address: string;
  landmark?: string;
  latitude?: number;
  longitude?: number;
  contactPhone?: string;
  contactEmail?: string;
  facilities: {
    parking?: boolean;
    food_court?: boolean;
    wheelchair_access?: boolean;
  };
  status: 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE';
  screens: Screen[];
}

// src/types/show.ts
export type ShowStatus = 'SCHEDULED' | 'CANCELLED' | 'COMPLETED' | 'HOUSEFULL';
export type SeatType = 'REGULAR' | 'PREMIUM' | 'RECLINER' | 'VIP' | 'WHEELCHAIR';

export interface Show {
  id: number;
  event: Pick<Movie, 'id' | 'title'>;
  screen: Screen & { venue: Pick<Venue, 'id' | 'name'> };
  showDate: string;
  startTime: string;
  endTime: string;
  status: ShowStatus;
  pricing: Record<SeatType, number>;
}

export interface Seat {
  id: number;
  rowName: string;
  seatNumber: number;
  seatLabel: string;
  seatType: SeatType;
  xPosition: number;
  yPosition: number;
  isAvailable: boolean;
}

export interface SeatAvailability {
  showId: number;
  seats: Seat[];
  unavailableSeatIds: number[];
  pricing: Record<SeatType, number>;
}

// src/types/booking.ts
export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED';
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED' | 'PARTIAL_REFUND';

export interface BookedSeat {
  seatId: number;
  seatLabel: string;
  seatType: SeatType;
  price: number;
}

export interface Ticket {
  id: number;
  ticketNumber: string;
  seatLabel: string;
  qrCode: string;
  status: 'VALID' | 'USED' | 'CANCELLED' | 'EXPIRED';
}

export interface Booking {
  id: number;
  bookingNumber: string;
  status: BookingStatus;
  paymentStatus?: PaymentStatus;
  totalAmount: number;
  convenienceFee: number;
  taxAmount: number;
  discountAmount: number;
  finalAmount: number;
  bookedSeats: BookedSeat[];
  tickets?: Ticket[];
  expiresAt?: string;
  bookedAt?: string;
  createdAt: string;
}

export interface BookingRequest {
  showId: number;
  seatIds: number[];
}

// src/types/payment.ts
export type PaymentMethod = 'UPI' | 'CREDIT_CARD' | 'DEBIT_CARD' | 'NETBANKING' | 'WALLET';

export interface PaymentRequest {
  bookingId: number;
  amount: number;
  paymentMethod: PaymentMethod;
  paymentGateway: string;
}

export interface Payment {
  id: number;
  bookingId: number;
  amount: number;
  paymentMethod: PaymentMethod;
  status: 'INITIATED' | 'PROCESSING' | 'SUCCESS' | 'FAILED' | 'REFUND_INITIATED' | 'REFUNDED';
  gatewayOrderId: string;
  gatewayPaymentId?: string;
  initiatedAt: string;
  completedAt?: string;
}
```

### Redux Store Configuration

```typescript
// src/store/index.ts
import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';
import { baseApi } from './api/base-api';
import authReducer from './slices/auth-slice';
import bookingReducer from './slices/booking-slice';
import cityReducer from './slices/city-slice';
import uiReducer from './slices/ui-slice';

export const store = configureStore({
  reducer: {
    [baseApi.reducerPath]: baseApi.reducer,
    auth: authReducer,
    booking: bookingReducer,
    city: cityReducer,
    ui: uiReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(baseApi.middleware),
});

setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

```typescript
// src/store/api/base-api.ts
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { RootState } from '../index';

const baseQuery = fetchBaseQuery({
  baseUrl: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
  prepareHeaders: (headers, { getState }) => {
    const token = (getState() as RootState).auth.token;
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }
    headers.set('Content-Type', 'application/json');
    return headers;
  },
});

export const baseApi = createApi({
  reducerPath: 'api',
  baseQuery,
  tagTypes: ['User', 'Movies', 'Venues', 'Shows', 'Bookings', 'Payments'],
  endpoints: () => ({}),
});
```

```typescript
// src/store/api/movies-api.ts
import { baseApi } from './base-api';
import type { ApiResponse, PaginatedResponse } from '@/types/api';
import type { Movie } from '@/types/movie';

export const moviesApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getMovies: builder.query<PaginatedResponse<Movie>, { page?: number; size?: number; status?: string }>({
      query: ({ page = 0, size = 20, status }) => ({
        url: '/v1/events',
        params: { page, size, status, category: 'MOVIE' },
      }),
      transformResponse: (response: ApiResponse<PaginatedResponse<Movie>>) => response.data,
      providesTags: ['Movies'],
    }),

    getMovieById: builder.query<Movie, number>({
      query: (id) => `/v1/events/${id}`,
      transformResponse: (response: ApiResponse<Movie>) => response.data,
      providesTags: (_, __, id) => [{ type: 'Movies', id }],
    }),

    getNowShowing: builder.query<Movie[], { cityId: number }>({
      query: ({ cityId }) => ({
        url: '/v1/events/now-showing',
        params: { cityId },
      }),
      transformResponse: (response: ApiResponse<Movie[]>) => response.data,
      providesTags: ['Movies'],
    }),

    getComingSoon: builder.query<Movie[], void>({
      query: () => '/v1/events/coming-soon',
      transformResponse: (response: ApiResponse<Movie[]>) => response.data,
      providesTags: ['Movies'],
    }),
  }),
});

export const {
  useGetMoviesQuery,
  useGetMovieByIdQuery,
  useGetNowShowingQuery,
  useGetComingSoonQuery,
} = moviesApi;
```

```typescript
// src/store/api/bookings-api.ts
import { baseApi } from './base-api';
import type { ApiResponse } from '@/types/api';
import type { Booking, BookingRequest, SeatAvailability } from '@/types/booking';

export const bookingsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getSeatAvailability: builder.query<SeatAvailability, number>({
      query: (showId) => `/v1/bookings/shows/${showId}/seats`,
      transformResponse: (response: ApiResponse<SeatAvailability>) => response.data,
      providesTags: (_, __, showId) => [{ type: 'Shows', id: showId }],
    }),

    initiateBooking: builder.mutation<Booking, BookingRequest>({
      query: (body) => ({
        url: '/v1/bookings',
        method: 'POST',
        body,
      }),
      transformResponse: (response: ApiResponse<Booking>) => response.data,
      invalidatesTags: ['Bookings'],
    }),

    confirmBooking: builder.mutation<Booking, { bookingId: number; amount: number }>({
      query: ({ bookingId, amount }) => ({
        url: `/v1/bookings/${bookingId}/confirm`,
        method: 'POST',
        body: { amount },
      }),
      transformResponse: (response: ApiResponse<Booking>) => response.data,
      invalidatesTags: ['Bookings'],
    }),

    cancelBooking: builder.mutation<Booking, { bookingId: number; reason?: string }>({
      query: ({ bookingId, reason }) => ({
        url: `/v1/bookings/${bookingId}/cancel`,
        method: 'POST',
        body: { reason },
      }),
      transformResponse: (response: ApiResponse<Booking>) => response.data,
      invalidatesTags: ['Bookings'],
    }),

    getBookingById: builder.query<Booking, number>({
      query: (id) => `/v1/bookings/${id}`,
      transformResponse: (response: ApiResponse<Booking>) => response.data,
      providesTags: (_, __, id) => [{ type: 'Bookings', id }],
    }),

    getUserBookings: builder.query<Booking[], { page?: number; size?: number }>({
      query: ({ page = 0, size = 10 }) => ({
        url: '/v1/bookings',
        params: { page, size },
      }),
      transformResponse: (response: ApiResponse<Booking[]>) => response.data,
      providesTags: ['Bookings'],
    }),
  }),
});

export const {
  useGetSeatAvailabilityQuery,
  useInitiateBookingMutation,
  useConfirmBookingMutation,
  useCancelBookingMutation,
  useGetBookingByIdQuery,
  useGetUserBookingsQuery,
} = bookingsApi;
```

```typescript
// src/store/slices/booking-slice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { Booking, Seat } from '@/types/booking';

interface BookingState {
  currentBooking: Booking | null;
  selectedSeats: Seat[];
  showId: number | null;
  expiresAt: string | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: BookingState = {
  currentBooking: null,
  selectedSeats: [],
  showId: null,
  expiresAt: null,
  isLoading: false,
  error: null,
};

const bookingSlice = createSlice({
  name: 'booking',
  initialState,
  reducers: {
    setShowId: (state, action: PayloadAction<number>) => {
      state.showId = action.payload;
      state.selectedSeats = [];
    },
    toggleSeat: (state, action: PayloadAction<Seat>) => {
      const seat = action.payload;
      const index = state.selectedSeats.findIndex((s) => s.id === seat.id);
      if (index >= 0) {
        state.selectedSeats.splice(index, 1);
      } else if (state.selectedSeats.length < 10) {
        state.selectedSeats.push(seat);
      }
    },
    clearSelectedSeats: (state) => {
      state.selectedSeats = [];
    },
    setCurrentBooking: (state, action: PayloadAction<Booking>) => {
      state.currentBooking = action.payload;
      state.expiresAt = action.payload.expiresAt || null;
    },
    clearBooking: (state) => {
      state.currentBooking = null;
      state.selectedSeats = [];
      state.showId = null;
      state.expiresAt = null;
      state.error = null;
    },
    setError: (state, action: PayloadAction<string>) => {
      state.error = action.payload;
    },
  },
});

export const {
  setShowId,
  toggleSeat,
  clearSelectedSeats,
  setCurrentBooking,
  clearBooking,
  setError,
} = bookingSlice.actions;

export default bookingSlice.reducer;
```

### Key Components

```typescript
// src/components/booking/seat-layout.tsx
'use client';

import { useMemo } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { toggleSeat } from '@/store/slices/booking-slice';
import { Seat } from './seat';
import { SeatLegend } from './seat-legend';
import type { Seat as SeatType, SeatAvailability } from '@/types/booking';
import { cn } from '@/lib/utils/cn';

interface SeatLayoutProps {
  seatData: SeatAvailability;
  maxSeats?: number;
}

export function SeatLayout({ seatData, maxSeats = 10 }: SeatLayoutProps) {
  const dispatch = useAppDispatch();
  const selectedSeats = useAppSelector((state) => state.booking.selectedSeats);

  // Group seats by row
  const seatsByRow = useMemo(() => {
    const grouped = new Map<string, SeatType[]>();
    seatData.seats.forEach((seat) => {
      const row = grouped.get(seat.rowName) || [];
      row.push(seat);
      grouped.set(seat.rowName, row);
    });
    // Sort seats within each row by seat number
    grouped.forEach((seats) => seats.sort((a, b) => a.seatNumber - b.seatNumber));
    return grouped;
  }, [seatData.seats]);

  const handleSeatClick = (seat: SeatType) => {
    if (seatData.unavailableSeatIds.includes(seat.id)) return;
    if (!seat.isAvailable) return;
    dispatch(toggleSeat(seat));
  };

  const isSeatSelected = (seatId: number) =>
    selectedSeats.some((s) => s.id === seatId);

  const isSeatUnavailable = (seatId: number) =>
    seatData.unavailableSeatIds.includes(seatId);

  return (
    <div className="flex flex-col items-center gap-8">
      {/* Screen */}
      <div className="w-full max-w-2xl">
        <div className="h-2 bg-gradient-to-r from-transparent via-primary to-transparent rounded-full" />
        <p className="text-center text-sm text-muted-foreground mt-2">SCREEN</p>
      </div>

      {/* Seat Grid */}
      <div className="flex flex-col gap-2">
        {Array.from(seatsByRow.entries()).map(([rowName, seats]) => (
          <div key={rowName} className="flex items-center gap-2">
            <span className="w-6 text-center text-sm font-medium">{rowName}</span>
            <div className="flex gap-1">
              {seats.map((seat) => (
                <Seat
                  key={seat.id}
                  seat={seat}
                  isSelected={isSeatSelected(seat.id)}
                  isUnavailable={isSeatUnavailable(seat.id) || !seat.isAvailable}
                  price={seatData.pricing[seat.seatType]}
                  onClick={() => handleSeatClick(seat)}
                  disabled={
                    (selectedSeats.length >= maxSeats && !isSeatSelected(seat.id)) ||
                    isSeatUnavailable(seat.id) ||
                    !seat.isAvailable
                  }
                />
              ))}
            </div>
            <span className="w-6 text-center text-sm font-medium">{rowName}</span>
          </div>
        ))}
      </div>

      {/* Legend */}
      <SeatLegend />

      {/* Selection Info */}
      {selectedSeats.length > 0 && (
        <div className="text-center">
          <p className="text-sm text-muted-foreground">
            Selected: {selectedSeats.map((s) => s.seatLabel).join(', ')}
          </p>
          <p className="text-lg font-semibold">
            {selectedSeats.length} seat{selectedSeats.length > 1 ? 's' : ''} selected
          </p>
        </div>
      )}
    </div>
  );
}
```

```typescript
// src/components/booking/seat.tsx
'use client';

import { cn } from '@/lib/utils/cn';
import type { Seat as SeatType, SeatType as SeatTypeEnum } from '@/types/booking';

interface SeatProps {
  seat: SeatType;
  isSelected: boolean;
  isUnavailable: boolean;
  price: number;
  onClick: () => void;
  disabled?: boolean;
}

const seatTypeColors: Record<SeatTypeEnum, string> = {
  REGULAR: 'bg-green-500 hover:bg-green-600',
  PREMIUM: 'bg-blue-500 hover:bg-blue-600',
  RECLINER: 'bg-purple-500 hover:bg-purple-600',
  VIP: 'bg-yellow-500 hover:bg-yellow-600',
  WHEELCHAIR: 'bg-teal-500 hover:bg-teal-600',
};

export function Seat({ seat, isSelected, isUnavailable, price, onClick, disabled }: SeatProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled || isUnavailable}
      title={`${seat.seatLabel} - ${seat.seatType} - ₹${price}`}
      className={cn(
        'w-8 h-8 rounded-t-lg text-xs font-medium transition-all',
        'focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2',
        isUnavailable && 'bg-gray-300 cursor-not-allowed opacity-50',
        isSelected && 'bg-primary text-primary-foreground ring-2 ring-primary',
        !isSelected && !isUnavailable && seatTypeColors[seat.seatType],
        !isSelected && !isUnavailable && 'text-white cursor-pointer',
        disabled && !isUnavailable && 'opacity-50 cursor-not-allowed'
      )}
    >
      {seat.seatNumber}
    </button>
  );
}
```

```typescript
// src/components/booking/booking-timer.tsx
'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { clearBooking } from '@/store/slices/booking-slice';
import { AlertTriangle } from 'lucide-react';
import { cn } from '@/lib/utils/cn';

export function BookingTimer() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const expiresAt = useAppSelector((state) => state.booking.expiresAt);
  const [timeLeft, setTimeLeft] = useState<number>(0);

  useEffect(() => {
    if (!expiresAt) return;

    const calculateTimeLeft = () => {
      const expiry = new Date(expiresAt).getTime();
      const now = Date.now();
      return Math.max(0, Math.floor((expiry - now) / 1000));
    };

    setTimeLeft(calculateTimeLeft());

    const interval = setInterval(() => {
      const remaining = calculateTimeLeft();
      setTimeLeft(remaining);

      if (remaining <= 0) {
        clearInterval(interval);
        dispatch(clearBooking());
        router.push('/booking-expired');
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [expiresAt, dispatch, router]);

  if (!expiresAt || timeLeft <= 0) return null;

  const minutes = Math.floor(timeLeft / 60);
  const seconds = timeLeft % 60;
  const isUrgent = timeLeft < 120; // Less than 2 minutes

  return (
    <div
      className={cn(
        'fixed top-20 right-4 z-50 px-4 py-2 rounded-lg shadow-lg flex items-center gap-2',
        isUrgent ? 'bg-red-500 text-white animate-pulse' : 'bg-yellow-500 text-black'
      )}
    >
      {isUrgent && <AlertTriangle className="h-4 w-4" />}
      <span className="font-mono font-bold">
        {String(minutes).padStart(2, '0')}:{String(seconds).padStart(2, '0')}
      </span>
      <span className="text-sm">remaining</span>
    </div>
  );
}
```

```typescript
// src/hooks/use-countdown.ts
import { useState, useEffect, useCallback } from 'react';

interface UseCountdownOptions {
  targetDate: string | Date;
  onExpire?: () => void;
}

interface CountdownResult {
  timeLeft: number;
  minutes: number;
  seconds: number;
  isExpired: boolean;
  isUrgent: boolean;
}

export function useCountdown({ targetDate, onExpire }: UseCountdownOptions): CountdownResult {
  const calculateTimeLeft = useCallback(() => {
    const target = new Date(targetDate).getTime();
    const now = Date.now();
    return Math.max(0, Math.floor((target - now) / 1000));
  }, [targetDate]);

  const [timeLeft, setTimeLeft] = useState<number>(calculateTimeLeft());

  useEffect(() => {
    const timer = setInterval(() => {
      const remaining = calculateTimeLeft();
      setTimeLeft(remaining);

      if (remaining <= 0) {
        clearInterval(timer);
        onExpire?.();
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [calculateTimeLeft, onExpire]);

  return {
    timeLeft,
    minutes: Math.floor(timeLeft / 60),
    seconds: timeLeft % 60,
    isExpired: timeLeft <= 0,
    isUrgent: timeLeft > 0 && timeLeft < 120,
  };
}
```

### Utility Functions

```typescript
// src/lib/utils/format.ts
import { format, formatDistanceToNow, parseISO } from 'date-fns';

export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(amount);
}

export function formatDate(date: string | Date): string {
  const d = typeof date === 'string' ? parseISO(date) : date;
  return format(d, 'EEE, dd MMM yyyy');
}

export function formatTime(time: string): string {
  // Expects HH:mm:ss format
  const [hours, minutes] = time.split(':');
  const hour = parseInt(hours, 10);
  const ampm = hour >= 12 ? 'PM' : 'AM';
  const displayHour = hour % 12 || 12;
  return `${displayHour}:${minutes} ${ampm}`;
}

export function formatDuration(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours === 0) return `${mins}m`;
  if (mins === 0) return `${hours}h`;
  return `${hours}h ${mins}m`;
}

export function formatRelativeTime(date: string | Date): string {
  const d = typeof date === 'string' ? parseISO(date) : date;
  return formatDistanceToNow(d, { addSuffix: true });
}
```

```typescript
// src/lib/utils/validation.ts
import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

export const registerSchema = z.object({
  email: z.string().email('Invalid email address'),
  fullName: z.string().min(2, 'Name must be at least 2 characters'),
  password: z
    .string()
    .min(8, 'Password must be at least 8 characters')
    .regex(/[A-Z]/, 'Password must contain an uppercase letter')
    .regex(/[a-z]/, 'Password must contain a lowercase letter')
    .regex(/[0-9]/, 'Password must contain a number'),
  confirmPassword: z.string(),
  phone: z.string().optional(),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});

export const bookingSchema = z.object({
  showId: z.number().positive(),
  seatIds: z.array(z.number().positive()).min(1, 'Select at least one seat').max(10, 'Maximum 10 seats allowed'),
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;
export type BookingFormData = z.infer<typeof bookingSchema>;
```

---

## Implementation Guide

### Phase 1: Project Setup

```bash
# 1. Create Next.js project
npx create-next-app@latest cinehub-frontend --typescript --tailwind --eslint --app --src-dir

# 2. Install dependencies
cd cinehub-frontend
npm install @reduxjs/toolkit react-redux
npm install react-hook-form @hookform/resolvers zod
npm install @radix-ui/react-dialog @radix-ui/react-dropdown-menu @radix-ui/react-tabs
npm install lucide-react date-fns clsx tailwind-merge
npm install framer-motion
npm install next-auth@beta

# 3. Install shadcn/ui
npx shadcn@latest init
npx shadcn@latest add button card input label select skeleton badge tabs dialog dropdown-menu toast

# 4. Install dev dependencies
npm install -D @types/node prettier eslint-config-prettier
```

### Phase 2: Implementation Order

1. **Core Setup**
   - Configure environment variables
   - Setup Redux store and provider
   - Create base API configuration
   - Setup authentication (NextAuth)

2. **Layout Components**
   - Header with navigation
   - Footer
   - City selector modal
   - Mobile navigation

3. **Home Page**
   - Movie carousel (Now Showing)
   - Coming Soon section
   - Search bar

4. **Movies Flow**
   - Movies list page with filters
   - Movie details page
   - Show times component

5. **Booking Flow (CRITICAL)**
   - Seat layout component
   - Seat selection logic
   - Booking summary
   - Timer component
   - Checkout page

6. **Authentication**
   - Login page
   - Register page
   - Protected routes

7. **User Profile**
   - Profile page
   - Booking history
   - Booking details with QR

---

## Edge Cases & Solutions

### 1. Concurrent Seat Selection
**Problem:** Two users select the same seat simultaneously.
**Solution:**
- Frontend shows real-time availability (refetch every 10 seconds)
- Backend returns specific error for conflicting seats
- Show which seats failed and allow user to reselect

```typescript
// Handle seat conflict error
if (error.errorCode === 'SEAT_UNAVAILABLE') {
  toast.error('Some seats are no longer available. Please reselect.');
  // Refetch seat availability
  refetch();
  dispatch(clearSelectedSeats());
}
```

### 2. Booking Timer Expiry
**Problem:** User doesn't complete payment in time.
**Solution:**
- Prominent countdown timer (10 minutes)
- Warning at 2 minutes remaining
- Auto-redirect when expired
- Clear booking state

```typescript
// In BookingTimer component
if (remaining <= 0) {
  dispatch(clearBooking());
  router.push('/booking-expired?reason=timeout');
}
```

### 3. Page Refresh During Booking
**Problem:** User refreshes page, loses selected seats.
**Solution:**
- Store booking ID in sessionStorage after initiation
- On mount, check for pending booking and resume
- Show "Resume Booking" prompt

```typescript
// On checkout page mount
useEffect(() => {
  const pendingBookingId = sessionStorage.getItem('pendingBookingId');
  if (pendingBookingId && !currentBooking) {
    // Fetch booking and restore state
    fetchBooking(parseInt(pendingBookingId));
  }
}, []);
```

### 4. Network Failure During Payment
**Problem:** Network fails after payment initiation.
**Solution:**
- Idempotent payment requests
- Retry mechanism with exponential backoff
- Check payment status on reconnect
- Show clear status to user

### 5. Browser Back Button
**Problem:** User clicks back during checkout, seat lock still active.
**Solution:**
- Use `beforeunload` event to warn user
- Provide explicit "Cancel Booking" button
- Handle route changes in booking flow

```typescript
// Warn user before leaving checkout
useEffect(() => {
  const handleBeforeUnload = (e: BeforeUnloadEvent) => {
    if (currentBooking?.status === 'PENDING') {
      e.preventDefault();
      e.returnValue = '';
    }
  };
  window.addEventListener('beforeunload', handleBeforeUnload);
  return () => window.removeEventListener('beforeunload', handleBeforeUnload);
}, [currentBooking]);
```

### 6. Mobile Responsiveness for Seat Layout
**Problem:** Seat grid is too wide for mobile screens.
**Solution:**
- Horizontal scroll with touch support
- Pinch-to-zoom on seat layout
- Landscape mode suggestion for booking

```typescript
// Responsive seat layout
<div className="overflow-x-auto touch-pan-x">
  <div className="min-w-max">
    {/* Seat grid */}
  </div>
</div>
```

### 7. Session Expiry
**Problem:** JWT token expires during booking.
**Solution:**
- Implement token refresh mechanism
- Check token validity before critical actions
- Graceful redirect to login with return URL

---

## Environment Variables

```env
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_APP_NAME=CineHub
NEXT_PUBLIC_APP_URL=http://localhost:3000

# NextAuth
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=your-secret-key-here

# Payment Gateway (if needed)
NEXT_PUBLIC_RAZORPAY_KEY_ID=rzp_test_xxx
```

---

## Performance Optimizations

1. **Image Optimization**
   - Use Next.js `<Image>` component
   - Lazy load movie posters
   - Use appropriate image sizes

2. **Code Splitting**
   - Dynamic imports for heavy components
   - Route-based code splitting (automatic with App Router)

3. **Caching Strategy**
   - RTK Query cache for API responses
   - ISR for movie details pages
   - SSG for static pages

4. **Bundle Size**
   - Tree-shake unused components
   - Analyze bundle with `@next/bundle-analyzer`

---

## Testing Strategy

```bash
# Unit tests with Jest + React Testing Library
npm install -D jest @testing-library/react @testing-library/jest-dom

# E2E tests with Playwright
npm install -D @playwright/test
```

### Critical Test Cases

1. **Seat Selection Flow**
   - Select single seat
   - Select multiple seats
   - Deselect seat
   - Max seat limit
   - Unavailable seat interaction

2. **Booking Flow**
   - Successful booking creation
   - Booking confirmation
   - Timer expiry handling
   - Network error recovery

3. **Authentication**
   - Login success/failure
   - Protected route redirect
   - Token refresh

---

*Document Version: 1.0.0 | Last Updated: 2024-01-15*
