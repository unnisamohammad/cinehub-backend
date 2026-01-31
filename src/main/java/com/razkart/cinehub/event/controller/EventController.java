package com.razkart.cinehub.event.controller;

import com.razkart.cinehub.common.dto.ApiResponse;
import com.razkart.cinehub.event.dto.EventRequest;
import com.razkart.cinehub.event.dto.EventResponse;
import com.razkart.cinehub.event.entity.EventCategory;
import com.razkart.cinehub.event.service.EventService;
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
@RequestMapping("/v1/events")
@RequiredArgsConstructor
@Tag(name = "Event", description = "Event/Movie management APIs")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Create a new event", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventRequest request) {

        EventResponse event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(event, "Event created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Update an event", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {

        EventResponse event = eventService.updateEvent(id, request);
        return ResponseEntity.ok(ApiResponse.success(event, "Event updated successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    @Operation(summary = "Delete an event", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Event deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all events with pagination")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getAllEvents(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<EventResponse> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/now-showing")
    @Operation(summary = "Get now showing events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getNowShowingEvents() {
        List<EventResponse> events = eventService.getNowShowingEvents();
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/coming-soon")
    @Operation(summary = "Get coming soon events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getComingSoonEvents() {
        List<EventResponse> events = eventService.getComingSoonEvents();
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get events by category")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEventsByCategory(
            @PathVariable EventCategory category,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<EventResponse> events = eventService.getEventsByCategory(category, pageable);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/search")
    @Operation(summary = "Search events")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> searchEvents(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<EventResponse> events = eventService.searchEvents(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/language/{language}")
    @Operation(summary = "Get events by language")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByLanguage(
            @PathVariable String language) {

        List<EventResponse> events = eventService.getEventsByLanguage(language);
        return ResponseEntity.ok(ApiResponse.success(events));
    }
}
