package com.razkart.cinehub.search.dto;

import com.razkart.cinehub.event.dto.EventResponse;
import com.razkart.cinehub.venue.dto.VenueResponse;

import java.util.List;

public record SearchResponse(
        List<EventResponse> events,
        List<VenueResponse> venues,
        int totalEvents,
        int totalVenues
) {}
