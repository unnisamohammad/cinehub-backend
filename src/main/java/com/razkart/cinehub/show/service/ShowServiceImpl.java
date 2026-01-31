package com.razkart.cinehub.show.service;

import com.razkart.cinehub.common.exception.BusinessException;
import com.razkart.cinehub.common.exception.ResourceNotFoundException;
import com.razkart.cinehub.event.entity.Event;
import com.razkart.cinehub.event.repository.EventRepository;
import com.razkart.cinehub.show.dto.SeatAvailabilityResponse;
import com.razkart.cinehub.show.dto.ShowRequest;
import com.razkart.cinehub.show.dto.ShowResponse;
import com.razkart.cinehub.show.entity.Show;
import com.razkart.cinehub.show.entity.ShowPricing;
import com.razkart.cinehub.show.entity.ShowStatus;
import com.razkart.cinehub.show.repository.ShowRepository;
import com.razkart.cinehub.venue.entity.Screen;
import com.razkart.cinehub.venue.entity.Seat;
import com.razkart.cinehub.venue.entity.SeatType;
import com.razkart.cinehub.venue.repository.ScreenRepository;
import com.razkart.cinehub.venue.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final EventRepository eventRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public ShowResponse createShow(ShowRequest request) {
        log.info("Creating show for event: {} on screen: {}", request.eventId(), request.screenId());

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + request.eventId()));

        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + request.screenId()));

        if (showRepository.existsByScreenIdAndShowDateAndStartTime(
                request.screenId(), request.showDate(), request.startTime())) {
            throw new BusinessException("A show already exists at this time on this screen");
        }

        Show show = Show.builder()
                .event(event)
                .screen(screen)
                .showDate(request.showDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(ShowStatus.SCHEDULED)
                .build();

        for (ShowRequest.PricingRequest pricingRequest : request.pricing()) {
            ShowPricing pricing = ShowPricing.builder()
                    .seatType(SeatType.valueOf(pricingRequest.seatType()))
                    .price(pricingRequest.price())
                    .build();
            show.addPricing(pricing);
        }

        Show savedShow = showRepository.save(show);
        log.info("Show created with ID: {}", savedShow.getId());

        return ShowResponse.from(savedShow);
    }

    @Override
    @Transactional
    public ShowResponse updateShow(Long id, ShowRequest request) {
        log.info("Updating show: {}", id);

        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + id));

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + request.eventId()));

        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + request.screenId()));

        show.setEvent(event);
        show.setScreen(screen);
        show.setShowDate(request.showDate());
        show.setStartTime(request.startTime());
        show.setEndTime(request.endTime());

        show.getPricing().clear();
        for (ShowRequest.PricingRequest pricingRequest : request.pricing()) {
            ShowPricing pricing = ShowPricing.builder()
                    .seatType(SeatType.valueOf(pricingRequest.seatType()))
                    .price(pricingRequest.price())
                    .build();
            show.addPricing(pricing);
        }

        Show updatedShow = showRepository.save(show);
        log.info("Show updated: {}", id);

        return ShowResponse.from(updatedShow);
    }

    @Override
    public ShowResponse getShowById(Long id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + id));
        return ShowResponse.from(show);
    }

    @Override
    @Transactional
    public void deleteShow(Long id) {
        log.info("Deleting show: {}", id);
        if (!showRepository.existsById(id)) {
            throw new ResourceNotFoundException("Show not found: " + id);
        }
        showRepository.deleteById(id);
        log.info("Show deleted: {}", id);
    }

    @Override
    @Transactional
    public void cancelShow(Long id) {
        log.info("Cancelling show: {}", id);
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + id));

        if (show.getStatus() == ShowStatus.CANCELLED) {
            throw new BusinessException("Show is already cancelled");
        }

        show.setStatus(ShowStatus.CANCELLED);
        showRepository.save(show);
        log.info("Show cancelled: {}", id);
    }

    @Override
    public List<ShowResponse> getShowsByEventAndCity(Long eventId, Long cityId, LocalDate date) {
        return showRepository.findByEventAndCityAndDate(eventId, cityId, date).stream()
                .map(ShowResponse::from)
                .toList();
    }

    @Override
    public List<ShowResponse> getShowsByVenue(Long venueId, LocalDate date) {
        return showRepository.findByVenueAndDate(venueId, date).stream()
                .map(ShowResponse::from)
                .toList();
    }

    @Override
    public List<ShowResponse> getUpcomingShowsByEvent(Long eventId) {
        return showRepository.findUpcomingByEvent(eventId, LocalDate.now()).stream()
                .map(ShowResponse::from)
                .toList();
    }

    @Override
    public List<LocalDate> getAvailableDates(Long eventId, Long cityId) {
        return showRepository.findAvailableDatesByEventAndCity(eventId, cityId, LocalDate.now());
    }

    @Override
    public SeatAvailabilityResponse getSeatAvailability(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + showId));

        List<Seat> allSeats = seatRepository.findByScreenId(show.getScreen().getId());

        // Get pricing map
        Map<String, BigDecimal> pricing = new HashMap<>();
        for (ShowPricing sp : show.getPricing()) {
            pricing.put(sp.getSeatType().name(), sp.getPrice());
        }

        // For now, return empty unavailable set - will be populated by booking service
        Set<Long> unavailableSeatIds = new HashSet<>();

        return SeatAvailabilityResponse.from(showId, allSeats, unavailableSeatIds, pricing);
    }
}
