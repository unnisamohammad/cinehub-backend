package com.razkart.cinehub.search.service;

import com.razkart.cinehub.event.dto.EventResponse;
import com.razkart.cinehub.event.repository.EventRepository;
import com.razkart.cinehub.search.dto.SearchRequest;
import com.razkart.cinehub.search.dto.SearchResponse;
import com.razkart.cinehub.venue.dto.VenueResponse;
import com.razkart.cinehub.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    @Override
    public SearchResponse search(SearchRequest request, Pageable pageable) {
        log.info("Searching for: {}", request.query());

        // Search events
        Page<EventResponse> eventPage = eventRepository.searchEvents(request.query(), pageable)
                .map(EventResponse::from);

        // Search venues
        Page<VenueResponse> venuePage = venueRepository.searchVenues(request.query(), pageable)
                .map(VenueResponse::fromWithoutScreens);

        List<EventResponse> events = eventPage.getContent();
        List<VenueResponse> venues = venuePage.getContent();

        return new SearchResponse(
                events,
                venues,
                (int) eventPage.getTotalElements(),
                (int) venuePage.getTotalElements()
        );
    }
}
