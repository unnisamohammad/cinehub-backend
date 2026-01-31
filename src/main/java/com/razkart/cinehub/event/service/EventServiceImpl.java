package com.razkart.cinehub.event.service;

import com.razkart.cinehub.common.exception.BusinessException;
import com.razkart.cinehub.common.exception.ResourceNotFoundException;
import com.razkart.cinehub.event.dto.EventRequest;
import com.razkart.cinehub.event.dto.EventResponse;
import com.razkart.cinehub.event.entity.*;
import com.razkart.cinehub.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request) {
        log.info("Creating new event: {}", request.title());

        if (eventRepository.existsByTitle(request.title())) {
            throw new BusinessException("Event with this title already exists");
        }

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .language(request.language())
                .durationMinutes(request.durationMinutes())
                .rating(request.rating() != null ? request.rating() : Rating.UA)
                .genre(request.genre())
                .posterUrl(request.posterUrl())
                .bannerUrl(request.bannerUrl())
                .trailerUrl(request.trailerUrl())
                .releaseDate(request.releaseDate())
                .status(request.status() != null ? request.status() : EventStatus.COMING_SOON)
                .build();

        if (request.cast() != null) {
            for (EventRequest.CastRequest castRequest : request.cast()) {
                Cast cast = Cast.builder()
                        .personName(castRequest.personName())
                        .roleType(RoleType.valueOf(castRequest.roleType()))
                        .characterName(castRequest.characterName())
                        .imageUrl(castRequest.imageUrl())
                        .displayOrder(castRequest.displayOrder() != null ? castRequest.displayOrder() : 0)
                        .build();
                event.addCastMember(cast);
            }
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Event created with ID: {}", savedEvent.getId());

        return EventResponse.from(savedEvent);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        log.info("Updating event: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setCategory(request.category());
        event.setLanguage(request.language());
        event.setDurationMinutes(request.durationMinutes());
        if (request.rating() != null) event.setRating(request.rating());
        event.setGenre(request.genre());
        event.setPosterUrl(request.posterUrl());
        event.setBannerUrl(request.bannerUrl());
        event.setTrailerUrl(request.trailerUrl());
        event.setReleaseDate(request.releaseDate());
        if (request.status() != null) event.setStatus(request.status());

        if (request.cast() != null) {
            event.getCastMembers().clear();
            for (EventRequest.CastRequest castRequest : request.cast()) {
                Cast cast = Cast.builder()
                        .personName(castRequest.personName())
                        .roleType(RoleType.valueOf(castRequest.roleType()))
                        .characterName(castRequest.characterName())
                        .imageUrl(castRequest.imageUrl())
                        .displayOrder(castRequest.displayOrder() != null ? castRequest.displayOrder() : 0)
                        .build();
                event.addCastMember(cast);
            }
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated: {}", id);

        return EventResponse.from(updatedEvent);
    }

    @Override
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
        return EventResponse.from(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        log.info("Deleting event: {}", id);
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found: " + id);
        }
        eventRepository.deleteById(id);
        log.info("Event deleted: {}", id);
    }

    @Override
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable).map(EventResponse::from);
    }

    @Override
    public List<EventResponse> getNowShowingEvents() {
        return eventRepository.findByStatus(EventStatus.NOW_SHOWING).stream()
                .map(EventResponse::from)
                .toList();
    }

    @Override
    public List<EventResponse> getComingSoonEvents() {
        return eventRepository.findComingSoon(EventStatus.COMING_SOON, LocalDate.now()).stream()
                .map(EventResponse::from)
                .toList();
    }

    @Override
    public Page<EventResponse> getEventsByCategory(EventCategory category, Pageable pageable) {
        return eventRepository.findByCategoryAndStatus(category, EventStatus.NOW_SHOWING, pageable)
                .map(EventResponse::from);
    }

    @Override
    public Page<EventResponse> searchEvents(String query, Pageable pageable) {
        return eventRepository.searchEvents(query, pageable).map(EventResponse::from);
    }

    @Override
    public List<EventResponse> getEventsByLanguage(String language) {
        return eventRepository.findByLanguageAndStatus(language, EventStatus.NOW_SHOWING).stream()
                .map(EventResponse::from)
                .toList();
    }
}
