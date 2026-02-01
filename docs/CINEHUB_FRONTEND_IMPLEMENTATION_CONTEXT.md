# CineHub Frontend Implementation Context

> **Purpose:** This file is designed to be used as context for Claude Code to implement the CineHub Next.js frontend application.
> **Copy this entire file and paste it as context when starting the frontend project.**

---

## PROJECT OVERVIEW

You are building the frontend for **CineHub**, a movie ticketing platform similar to BookMyShow. The Spring Boot backend is already complete and running at `http://localhost:8080/api`.

### Your Goal
Create a production-ready Next.js 15 web application with:
- Server Components for SEO and performance
- TypeScript for type safety
- Redux Toolkit + RTK Query for state management
- Tailwind CSS + shadcn/ui for styling
- Complete booking flow with real-time seat selection

---

## TECHNOLOGY REQUIREMENTS

```json
{
  "framework": "Next.js 15+ (App Router)",
  "language": "TypeScript 5.x (strict mode)",
  "styling": "Tailwind CSS 3.x",
  "ui_components": "shadcn/ui (Radix UI based)",
  "state_management": "Redux Toolkit + RTK Query",
  "forms": "React Hook Form + Zod validation",
  "icons": "Lucide React",
  "animations": "Framer Motion",
  "date_handling": "date-fns"
}
```

---

## BACKEND API REFERENCE

**Base URL:** `http://localhost:8080/api`
**Authentication:** JWT Bearer Token in Authorization header

### API Response Format
```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode?: string;
  errors?: Record<string, string>;
  timestamp: string;
}

interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
```

### Authentication Endpoints

```typescript
// POST /api/v1/auth/register
interface RegisterRequest {
  email: string;
  fullName: string;
  password: string;
  phone?: string;
}

// POST /api/v1/auth/login
interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  token: string;
  refreshToken: string;
  user: {
    id: number;
    email: string;
    fullName: string;
    role: 'CUSTOMER' | 'THEATER_OWNER' | 'ADMIN';
  };
}

// GET /api/v1/users/me (Auth Required)
// PATCH /api/v1/users/me (Auth Required)
```

### Movie/Event Endpoints

```typescript
// GET /api/v1/events?page=0&size=20&category=MOVIE&status=NOW_SHOWING
// GET /api/v1/events/{id}
// GET /api/v1/events/now-showing?cityId=1
// GET /api/v1/events/coming-soon

interface Movie {
  id: number;
  title: string;
  description: string;
  category: 'MOVIE' | 'CONCERT' | 'SPORT' | 'PLAY' | 'COMEDY' | 'OTHER';
  language: string;
  durationMinutes: number;
  rating: 'U' | 'UA' | 'A' | 'S';
  genre: string;
  posterUrl: string;
  bannerUrl?: string;
  trailerUrl?: string;
  releaseDate: string;
  status: 'COMING_SOON' | 'NOW_SHOWING' | 'ENDED';
  avgRating: number;
  totalReviews: number;
}
```

### Venue Endpoints

```typescript
// GET /api/v1/cities
// GET /api/v1/cities/{cityId}/venues
// GET /api/v1/venues/{id}

interface City {
  id: number;
  name: string;
  state: string;
  isActive: boolean;
}

interface Venue {
  id: number;
  name: string;
  city: City;
  address: string;
  landmark?: string;
  facilities: { parking?: boolean; food_court?: boolean; wheelchair_access?: boolean };
  status: 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE';
  screens: Array<{
    id: number;
    name: string;
    screenType: 'REGULAR' | 'IMAX' | '4DX' | 'DOLBY_ATMOS' | 'PREMIUM' | 'GOLD';
    totalSeats: number;
  }>;
}
```

### Show Endpoints

```typescript
// GET /api/v1/shows/event/{eventId}?cityId=1&date=2024-01-15
// GET /api/v1/shows/{id}
// GET /api/v1/bookings/shows/{showId}/seats

interface Show {
  id: number;
  event: { id: number; title: string };
  screen: {
    id: number;
    name: string;
    screenType: string;
    venue: { id: number; name: string };
  };
  showDate: string;      // "2024-01-15"
  startTime: string;     // "14:30:00"
  endTime: string;       // "17:00:00"
  status: 'SCHEDULED' | 'CANCELLED' | 'COMPLETED' | 'HOUSEFULL';
  pricing: {
    REGULAR: number;
    PREMIUM: number;
    RECLINER: number;
    VIP: number;
    WHEELCHAIR: number;
  };
}

interface SeatAvailability {
  showId: number;
  seats: Array<{
    id: number;
    rowName: string;
    seatNumber: number;
    seatLabel: string;      // "A1", "B5"
    seatType: 'REGULAR' | 'PREMIUM' | 'RECLINER' | 'VIP' | 'WHEELCHAIR';
    xPosition: number;
    yPosition: number;
    isAvailable: boolean;
  }>;
  unavailableSeatIds: number[];  // IDs of booked/locked seats
  pricing: Record<string, number>;
}
```

### Booking Endpoints (CRITICAL)

```typescript
// POST /api/v1/bookings (Auth Required)
interface BookingRequest {
  showId: number;
  seatIds: number[];  // Max 10 seats
}

// POST /api/v1/bookings/{bookingId}/confirm (Auth Required)
interface ConfirmRequest {
  amount: number;  // Must match booking.finalAmount
}

// POST /api/v1/bookings/{bookingId}/cancel (Auth Required)
// GET /api/v1/bookings/{bookingId} (Auth Required)
// GET /api/v1/bookings (Auth Required) - User's bookings

interface Booking {
  id: number;
  bookingNumber: string;   // "CH1705312200001234"
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED';
  paymentStatus?: 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';
  totalAmount: number;
  convenienceFee: number;  // 5% of total
  taxAmount: number;       // 18% GST
  discountAmount: number;
  finalAmount: number;
  bookedSeats: Array<{
    seatId: number;
    seatLabel: string;
    seatType: string;
    price: number;
  }>;
  tickets?: Array<{
    id: number;
    ticketNumber: string;
    seatLabel: string;
    qrCode: string;        // Base64 encoded
    status: 'VALID' | 'USED' | 'CANCELLED' | 'EXPIRED';
  }>;
  expiresAt?: string;      // ISO datetime - 10 minutes from creation
  bookedAt?: string;
  createdAt: string;
}
```

### Error Codes

```typescript
type ErrorCode =
  | 'RESOURCE_NOT_FOUND'    // 404
  | 'SEAT_UNAVAILABLE'      // 409 - Seats already booked/locked
  | 'BOOKING_EXPIRED'       // 410 - Lock expired
  | 'PAYMENT_FAILED'        // 402
  | 'VALIDATION_ERROR'      // 400
  | 'INVALID_CREDENTIALS'   // 401
  | 'ACCESS_DENIED'         // 403
  | 'INTERNAL_ERROR';       // 500
```

---

## PROJECT STRUCTURE

Create this exact folder structure:

```
src/
├── app/
│   ├── layout.tsx                    # Root layout with providers
│   ├── page.tsx                      # Home page
│   ├── loading.tsx                   # Global loading
│   ├── error.tsx                     # Global error
│   ├── not-found.tsx                 # 404 page
│   │
│   ├── (auth)/                       # Auth route group (no header/footer)
│   │   ├── layout.tsx
│   │   ├── login/page.tsx
│   │   ├── register/page.tsx
│   │   └── forgot-password/page.tsx
│   │
│   ├── (main)/                       # Main route group (with header/footer)
│   │   ├── layout.tsx
│   │   ├── movies/
│   │   │   ├── page.tsx              # Movies list
│   │   │   └── [id]/
│   │   │       ├── page.tsx          # Movie details + shows
│   │   │       └── book/[showId]/
│   │   │           └── page.tsx      # Seat selection (Auth required)
│   │   ├── theaters/
│   │   │   ├── page.tsx
│   │   │   └── [id]/page.tsx
│   │   ├── search/page.tsx
│   │   └── checkout/[bookingId]/
│   │       ├── page.tsx              # Payment (Auth required)
│   │       ├── success/page.tsx
│   │       └── failed/page.tsx
│   │
│   └── (protected)/                  # Protected routes
│       ├── layout.tsx                # Auth check
│       └── profile/
│           ├── page.tsx
│           └── bookings/
│               ├── page.tsx
│               └── [id]/page.tsx
│
├── components/
│   ├── ui/                           # shadcn/ui components
│   ├── layout/
│   │   ├── header.tsx
│   │   ├── footer.tsx
│   │   ├── mobile-nav.tsx
│   │   └── city-selector.tsx
│   ├── movies/
│   │   ├── movie-card.tsx
│   │   ├── movie-grid.tsx
│   │   ├── movie-carousel.tsx
│   │   └── movie-filters.tsx
│   ├── shows/
│   │   ├── show-times.tsx
│   │   ├── date-selector.tsx
│   │   └── theater-shows.tsx
│   ├── booking/
│   │   ├── seat-layout.tsx           # CRITICAL
│   │   ├── seat.tsx
│   │   ├── seat-legend.tsx
│   │   ├── booking-summary.tsx
│   │   ├── booking-timer.tsx         # CRITICAL
│   │   ├── price-breakdown.tsx
│   │   └── ticket-card.tsx
│   ├── payment/
│   │   ├── payment-form.tsx
│   │   └── payment-methods.tsx
│   ├── auth/
│   │   ├── login-form.tsx
│   │   ├── register-form.tsx
│   │   └── user-menu.tsx
│   └── shared/
│       ├── loading-spinner.tsx
│       ├── empty-state.tsx
│       ├── rating-stars.tsx
│       └── price-tag.tsx
│
├── store/
│   ├── index.ts                      # Store configuration
│   ├── provider.tsx                  # Redux provider
│   ├── hooks.ts                      # Typed hooks
│   ├── slices/
│   │   ├── auth-slice.ts
│   │   ├── booking-slice.ts
│   │   ├── city-slice.ts
│   │   └── ui-slice.ts
│   └── api/
│       ├── base-api.ts               # RTK Query base
│       ├── auth-api.ts
│       ├── movies-api.ts
│       ├── venues-api.ts
│       ├── shows-api.ts
│       └── bookings-api.ts
│
├── hooks/
│   ├── use-auth.ts
│   ├── use-countdown.ts              # CRITICAL for booking timer
│   ├── use-city.ts
│   └── use-debounce.ts
│
├── lib/
│   ├── utils/
│   │   ├── cn.ts                     # clsx + tailwind-merge
│   │   ├── format.ts                 # Date, currency formatters
│   │   └── constants.ts
│   └── validation/
│       └── schemas.ts                # Zod schemas
│
├── types/
│   ├── index.ts
│   ├── api.ts
│   ├── movie.ts
│   ├── venue.ts
│   ├── show.ts
│   ├── booking.ts
│   └── auth.ts
│
└── styles/
    └── globals.css
```

---

## CRITICAL IMPLEMENTATION DETAILS

### 1. Seat Selection & Booking Flow

This is the most critical part of the application. Follow these rules:

```
USER FLOW:
1. User selects movie → sees available shows
2. User clicks show → goes to /movies/[id]/book/[showId]
3. Page loads seat availability from API
4. User selects seats (max 10) → seats highlight
5. User clicks "Continue" → API call POST /bookings
   - Backend locks seats for 10 MINUTES
   - Returns booking with expiresAt timestamp
6. Redirect to /checkout/[bookingId]
7. Display 10-minute countdown timer
8. User completes payment → POST /bookings/{id}/confirm
9. On success → show tickets with QR codes
10. On timer expiry → booking expires, redirect back
```

### 2. Booking Timer Implementation

```typescript
// CRITICAL: Must be accurate and handle edge cases

function useBookingTimer(expiresAt: string | null) {
  const [timeLeft, setTimeLeft] = useState<number>(0);
  const router = useRouter();
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (!expiresAt) return;

    const calculateTimeLeft = () => {
      const expiry = new Date(expiresAt).getTime();
      const now = Date.now();
      return Math.max(0, Math.floor((expiry - now) / 1000));
    };

    setTimeLeft(calculateTimeLeft());

    const timer = setInterval(() => {
      const remaining = calculateTimeLeft();
      setTimeLeft(remaining);

      if (remaining <= 0) {
        clearInterval(timer);
        dispatch(clearBooking());
        router.push('/booking-expired');
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [expiresAt]);

  return {
    minutes: Math.floor(timeLeft / 60),
    seconds: timeLeft % 60,
    isExpired: timeLeft <= 0,
    isUrgent: timeLeft > 0 && timeLeft < 120, // < 2 minutes
  };
}
```

### 3. Seat Layout Component

```typescript
// Group seats by row and render grid
// Handle selection state
// Show different colors for seat types
// Disable unavailable seats

const seatTypeColors = {
  REGULAR: 'bg-green-500',
  PREMIUM: 'bg-blue-500',
  RECLINER: 'bg-purple-500',
  VIP: 'bg-yellow-500',
  WHEELCHAIR: 'bg-teal-500',
};

// Selected seat: bg-primary with ring
// Unavailable seat: bg-gray-300 opacity-50 cursor-not-allowed
```

### 4. Redux Booking Slice

```typescript
interface BookingState {
  currentBooking: Booking | null;
  selectedSeats: Seat[];
  showId: number | null;
  expiresAt: string | null;
}

// Actions:
// - setShowId(id)
// - toggleSeat(seat) - add or remove from selection
// - clearSelectedSeats()
// - setCurrentBooking(booking)
// - clearBooking()
```

### 5. Protected Routes

```typescript
// src/app/(protected)/layout.tsx
// Check if user is authenticated
// If not, redirect to /login?redirect={currentPath}
// Show loading state while checking
```

### 6. City Selection

- Store selected city in localStorage
- Show city selector modal on first visit
- Header should display current city
- Filter movies and shows by city

---

## STYLING GUIDELINES

### Color Scheme
```css
:root {
  --primary: #e11d48;        /* Rose-600 - Brand color */
  --primary-foreground: #ffffff;
  --background: #09090b;     /* Zinc-950 - Dark background */
  --foreground: #fafafa;     /* Zinc-50 */
  --card: #18181b;           /* Zinc-900 */
  --card-foreground: #fafafa;
  --muted: #27272a;          /* Zinc-800 */
  --muted-foreground: #a1a1aa; /* Zinc-400 */
  --accent: #27272a;
  --destructive: #ef4444;    /* Red-500 */
}
```

### Component Styling
- Use Tailwind classes exclusively
- Use `cn()` utility for conditional classes
- Mobile-first responsive design
- Dark theme by default

---

## IMPLEMENTATION ORDER

Execute in this exact order:

### Phase 1: Setup (Start Here)
1. Create Next.js project with TypeScript and Tailwind
2. Install all dependencies
3. Configure environment variables
4. Setup shadcn/ui
5. Create types directory with all interfaces
6. Setup Redux store with RTK Query

### Phase 2: Layout & Navigation
1. Create header with logo, search, city selector, user menu
2. Create footer
3. Create mobile navigation
4. Setup route groups and layouts

### Phase 3: Home & Movies
1. Home page with movie carousels
2. Movies list page with filters
3. Movie details page
4. Movie card component

### Phase 4: Shows & Theaters
1. Show times component
2. Date selector
3. Theater shows grouping
4. Venue pages

### Phase 5: Booking (CRITICAL)
1. Seat layout component
2. Seat selection logic
3. Booking summary
4. Booking timer
5. Checkout page
6. Success/failure pages

### Phase 6: Authentication
1. Login form
2. Register form
3. Auth guard
4. User menu

### Phase 7: Profile
1. Profile page
2. Booking history
3. Booking details with QR

---

## EDGE CASES TO HANDLE

1. **Seats become unavailable during selection**
   - Refetch seat availability every 10 seconds
   - Show toast if selected seat becomes unavailable
   - Clear conflicting seats from selection

2. **Booking timer expires**
   - Show warning at 2 minutes
   - Auto-redirect when expired
   - Clear booking state

3. **Network failure during payment**
   - Show retry option
   - Don't duplicate payment
   - Check booking status on reconnect

4. **Back button during checkout**
   - Warn user about losing booking
   - Provide explicit cancel option

5. **Page refresh during booking**
   - Store booking ID in sessionStorage
   - Resume booking on page load if valid

6. **Mobile seat selection**
   - Horizontal scroll for seat layout
   - Touch-friendly seat buttons
   - Suggest landscape mode

---

## ENVIRONMENT VARIABLES

```env
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_APP_NAME=CineHub
```

---

## COMMANDS TO RUN

```bash
# Create project
npx create-next-app@latest cinehub-frontend --typescript --tailwind --eslint --app --src-dir

# Install dependencies
npm install @reduxjs/toolkit react-redux
npm install react-hook-form @hookform/resolvers zod
npm install lucide-react date-fns clsx tailwind-merge
npm install framer-motion

# Setup shadcn/ui
npx shadcn@latest init
npx shadcn@latest add button card input label select skeleton badge tabs dialog dropdown-menu toast avatar separator scroll-area sheet

# Run development server
npm run dev
```

---

## QUALITY CHECKLIST

Before completing each component, verify:

- [ ] TypeScript types are correct and complete
- [ ] Loading states are handled
- [ ] Error states are handled
- [ ] Empty states are handled
- [ ] Mobile responsive
- [ ] Accessible (keyboard navigation, ARIA)
- [ ] Console has no errors/warnings
- [ ] API calls use RTK Query
- [ ] Forms use React Hook Form + Zod

---

## SAMPLE API CALLS

```typescript
// Fetch movies
const { data: movies, isLoading, error } = useGetNowShowingQuery({ cityId: 1 });

// Initiate booking
const [initiateBooking, { isLoading }] = useInitiateBookingMutation();
const result = await initiateBooking({ showId: 1, seatIds: [1, 2, 3] });

// Confirm booking
const [confirmBooking] = useConfirmBookingMutation();
await confirmBooking({ bookingId: 1, amount: 929.25 });
```

---

**START IMPLEMENTATION NOW**

Begin with Phase 1: Setup. Create the project structure and install dependencies. Then proceed through each phase in order. Ask clarifying questions if any API behavior is unclear.
