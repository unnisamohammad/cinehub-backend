package com.razkart.cinehub.venue.controller;

import com.razkart.cinehub.common.dto.ApiResponse;
import com.razkart.cinehub.venue.dto.*;
import com.razkart.cinehub.venue.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Venue", description = "Venue/Theater management APIs")
public class VenueController {

    private final VenueService venueService;

    // City endpoints
    @GetMapping("/cities")
    @Operation(summary = "Get all active cities")
    public ResponseEntity<ApiResponse<List<CityResponse>>> getActiveCities() {
        List<CityResponse> cities = venueService.getActiveCities();
        return ResponseEntity.ok(ApiResponse.success(cities));
    }

    @GetMapping("/cities/{id}")
    @Operation(summary = "Get city by ID")
    public ResponseEntity<ApiResponse<CityResponse>> getCityById(@PathVariable Long id) {
        CityResponse city = venueService.getCityById(id);
        return ResponseEntity.ok(ApiResponse.success(city));
    }

    // Venue endpoints
    @PostMapping("/venues")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Create a new venue", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<VenueResponse>> createVenue(
            @Valid @RequestBody VenueRequest request) {

        VenueResponse venue = venueService.createVenue(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(venue, "Venue created successfully"));
    }

    @PutMapping("/venues/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Update a venue", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<VenueResponse>> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueRequest request) {

        VenueResponse venue = venueService.updateVenue(id, request);
        return ResponseEntity.ok(ApiResponse.success(venue, "Venue updated successfully"));
    }

    @GetMapping("/venues/{id}")
    @Operation(summary = "Get venue by ID")
    public ResponseEntity<ApiResponse<VenueResponse>> getVenueById(@PathVariable Long id) {
        VenueResponse venue = venueService.getVenueById(id);
        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    @DeleteMapping("/venues/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a venue", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Venue deleted successfully"));
    }

    @GetMapping("/cities/{cityId}/venues")
    @Operation(summary = "Get venues in a city")
    public ResponseEntity<ApiResponse<List<VenueResponse>>> getVenuesByCity(
            @PathVariable Long cityId) {

        List<VenueResponse> venues = venueService.getVenuesByCity(cityId);
        return ResponseEntity.ok(ApiResponse.success(venues));
    }

    @GetMapping("/venues/search")
    @Operation(summary = "Search venues")
    public ResponseEntity<ApiResponse<Page<VenueResponse>>> searchVenues(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<VenueResponse> venues = venueService.searchVenues(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(venues));
    }

    @GetMapping("/events/{eventId}/venues")
    @Operation(summary = "Get venues showing an event in a city")
    public ResponseEntity<ApiResponse<List<VenueResponse>>> getVenuesShowingEvent(
            @PathVariable Long eventId,
            @RequestParam Long cityId) {

        List<VenueResponse> venues = venueService.getVenuesShowingEvent(eventId, cityId);
        return ResponseEntity.ok(ApiResponse.success(venues));
    }

    // Screen endpoints
    @GetMapping("/venues/{venueId}/screens")
    @Operation(summary = "Get screens in a venue")
    public ResponseEntity<ApiResponse<List<ScreenResponse>>> getScreensByVenue(
            @PathVariable Long venueId) {

        List<ScreenResponse> screens = venueService.getScreensByVenue(venueId);
        return ResponseEntity.ok(ApiResponse.success(screens));
    }

    @GetMapping("/screens/{screenId}")
    @Operation(summary = "Get screen by ID")
    public ResponseEntity<ApiResponse<ScreenResponse>> getScreenById(@PathVariable Long screenId) {
        ScreenResponse screen = venueService.getScreenById(screenId);
        return ResponseEntity.ok(ApiResponse.success(screen));
    }

    @GetMapping("/screens/{screenId}/seats")
    @Operation(summary = "Get seat layout for a screen")
    public ResponseEntity<ApiResponse<SeatLayoutResponse>> getSeatLayout(
            @PathVariable Long screenId) {

        SeatLayoutResponse layout = venueService.getSeatLayout(screenId);
        return ResponseEntity.ok(ApiResponse.success(layout));
    }
}
