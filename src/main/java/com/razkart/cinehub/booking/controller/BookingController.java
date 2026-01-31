package com.razkart.cinehub.booking.controller;

import com.razkart.cinehub.booking.dto.*;
import com.razkart.cinehub.booking.service.BookingService;
import com.razkart.cinehub.common.dto.ApiResponse;
import com.razkart.cinehub.show.dto.SeatAvailabilityResponse;
import com.razkart.cinehub.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
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

    @GetMapping("/number/{bookingNumber}")
    @Operation(summary = "Get booking by booking number")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingByNumber(
            @PathVariable String bookingNumber,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.getBookingByNumber(bookingNumber, currentUser.getId());
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

    @GetMapping("/tickets/{ticketNumber}")
    @Operation(summary = "Get ticket details")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicket(
            @PathVariable String ticketNumber,
            @AuthenticationPrincipal User currentUser) {

        TicketResponse ticket = bookingService.getTicket(ticketNumber, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    public record CancellationRequest(String reason) {}
}
