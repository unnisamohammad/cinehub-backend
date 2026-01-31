package com.razkart.cinehub.event.service;

import com.razkart.cinehub.event.dto.EventRequest;
import com.razkart.cinehub.event.dto.EventResponse;
import com.razkart.cinehub.event.entity.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {

    EventResponse createEvent(EventRequest request);

    EventResponse updateEvent(Long id, EventRequest request);

    EventResponse getEventById(Long id);

    void deleteEvent(Long id);

    Page<EventResponse> getAllEvents(Pageable pageable);

    List<EventResponse> getNowShowingEvents();

    List<EventResponse> getComingSoonEvents();

    Page<EventResponse> getEventsByCategory(EventCategory category, Pageable pageable);

    Page<EventResponse> searchEvents(String query, Pageable pageable);

    List<EventResponse> getEventsByLanguage(String language);
}
