package com.razkart.cinehub.show.controller;

import com.razkart.cinehub.common.dto.ApiResponse;
import com.razkart.cinehub.show.dto.SeatAvailabilityResponse;
import com.razkart.cinehub.show.dto.ShowRequest;
import com.razkart.cinehub.show.dto.ShowResponse;
import com.razkart.cinehub.show.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/shows")
@RequiredArgsConstructor
@Tag(name = "Show", description = "Show/Showtime management APIs")
public class ShowController {

    private final ShowService showService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Create a new show", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ShowResponse>> createShow(
            @Valid @RequestBody ShowRequest request) {

        ShowResponse show = showService.createShow(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(show, "Show created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Update a show", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ShowResponse>> updateShow(
            @PathVariable Long id,
            @Valid @RequestBody ShowRequest request) {

        ShowResponse show = showService.updateShow(id, request);
        return ResponseEntity.ok(ApiResponse.success(show, "Show updated successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get show by ID")
    public ResponseEntity<ApiResponse<ShowResponse>> getShowById(@PathVariable Long id) {
        ShowResponse show = showService.getShowById(id);
        return ResponseEntity.ok(ApiResponse.success(show));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Delete a show", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Show deleted successfully"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Cancel a show", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> cancelShow(@PathVariable Long id) {
        showService.cancelShow(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Show cancelled successfully"));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get shows for an event in a city on a date")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByEventAndCity(
            @PathVariable Long eventId,
            @RequestParam Long cityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShowResponse> shows = showService.getShowsByEventAndCity(eventId, cityId, date);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @GetMapping("/venue/{venueId}")
    @Operation(summary = "Get shows at a venue on a date")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByVenue(
            @PathVariable Long venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShowResponse> shows = showService.getShowsByVenue(venueId, date);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @GetMapping("/event/{eventId}/upcoming")
    @Operation(summary = "Get upcoming shows for an event")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getUpcomingShows(
            @PathVariable Long eventId) {

        List<ShowResponse> shows = showService.getUpcomingShowsByEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @GetMapping("/event/{eventId}/dates")
    @Operation(summary = "Get available dates for an event in a city")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getAvailableDates(
            @PathVariable Long eventId,
            @RequestParam Long cityId) {

        List<LocalDate> dates = showService.getAvailableDates(eventId, cityId);
        return ResponseEntity.ok(ApiResponse.success(dates));
    }

    @GetMapping("/{showId}/seats")
    @Operation(summary = "Get seat availability for a show")
    public ResponseEntity<ApiResponse<SeatAvailabilityResponse>> getSeatAvailability(
            @PathVariable Long showId) {

        SeatAvailabilityResponse availability = showService.getSeatAvailability(showId);
        return ResponseEntity.ok(ApiResponse.success(availability));
    }
}
