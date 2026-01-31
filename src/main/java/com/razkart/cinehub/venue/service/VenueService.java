package com.razkart.cinehub.venue.service;

import com.razkart.cinehub.venue.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VenueService {

    // City operations
    List<CityResponse> getActiveCities();

    CityResponse getCityById(Long id);

    // Venue operations
    VenueResponse createVenue(VenueRequest request);

    VenueResponse updateVenue(Long id, VenueRequest request);

    VenueResponse getVenueById(Long id);

    void deleteVenue(Long id);

    List<VenueResponse> getVenuesByCity(Long cityId);

    Page<VenueResponse> searchVenues(String query, Pageable pageable);

    List<VenueResponse> getVenuesShowingEvent(Long eventId, Long cityId);

    // Screen operations
    List<ScreenResponse> getScreensByVenue(Long venueId);

    ScreenResponse getScreenById(Long screenId);

    // Seat layout
    SeatLayoutResponse getSeatLayout(Long screenId);
}
