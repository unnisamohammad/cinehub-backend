package com.razkart.cinehub.venue.service;

import com.razkart.cinehub.common.exception.BusinessException;
import com.razkart.cinehub.common.exception.ResourceNotFoundException;
import com.razkart.cinehub.venue.dto.*;
import com.razkart.cinehub.venue.entity.*;
import com.razkart.cinehub.venue.repository.*;
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
public class VenueServiceImpl implements VenueService {

    private final CityRepository cityRepository;
    private final VenueRepository venueRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    @Override
    public List<CityResponse> getActiveCities() {
        return cityRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(CityResponse::from)
                .toList();
    }

    @Override
    public CityResponse getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
        return CityResponse.from(city);
    }

    @Override
    @Transactional
    public VenueResponse createVenue(VenueRequest request) {
        log.info("Creating new venue: {}", request.name());

        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + request.cityId()));

        if (venueRepository.existsByNameAndCityId(request.name(), request.cityId())) {
            throw new BusinessException("Venue with this name already exists in the city");
        }

        Venue venue = Venue.builder()
                .name(request.name())
                .city(city)
                .address(request.address())
                .landmark(request.landmark())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .contactPhone(request.contactPhone())
                .contactEmail(request.contactEmail())
                .facilities(request.facilities())
                .status(VenueStatus.ACTIVE)
                .build();

        Venue savedVenue = venueRepository.save(venue);
        log.info("Venue created with ID: {}", savedVenue.getId());

        return VenueResponse.from(savedVenue);
    }

    @Override
    @Transactional
    public VenueResponse updateVenue(Long id, VenueRequest request) {
        log.info("Updating venue: {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + id));

        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + request.cityId()));

        venue.setName(request.name());
        venue.setCity(city);
        venue.setAddress(request.address());
        venue.setLandmark(request.landmark());
        venue.setLatitude(request.latitude());
        venue.setLongitude(request.longitude());
        venue.setContactPhone(request.contactPhone());
        venue.setContactEmail(request.contactEmail());
        venue.setFacilities(request.facilities());

        Venue updatedVenue = venueRepository.save(venue);
        log.info("Venue updated: {}", id);

        return VenueResponse.from(updatedVenue);
    }

    @Override
    public VenueResponse getVenueById(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + id));
        return VenueResponse.from(venue);
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        log.info("Deleting venue: {}", id);
        if (!venueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venue not found: " + id);
        }
        venueRepository.deleteById(id);
        log.info("Venue deleted: {}", id);
    }

    @Override
    public List<VenueResponse> getVenuesByCity(Long cityId) {
        return venueRepository.findByCityIdAndStatus(cityId, VenueStatus.ACTIVE).stream()
                .map(VenueResponse::fromWithoutScreens)
                .toList();
    }

    @Override
    public Page<VenueResponse> searchVenues(String query, Pageable pageable) {
        return venueRepository.searchVenues(query, pageable)
                .map(VenueResponse::fromWithoutScreens);
    }

    @Override
    public List<VenueResponse> getVenuesShowingEvent(Long eventId, Long cityId) {
        return venueRepository.findVenuesShowingEvent(eventId, cityId).stream()
                .map(VenueResponse::fromWithoutScreens)
                .toList();
    }

    @Override
    public List<ScreenResponse> getScreensByVenue(Long venueId) {
        return screenRepository.findByVenueIdAndIsActiveTrue(venueId).stream()
                .map(ScreenResponse::from)
                .toList();
    }

    @Override
    public ScreenResponse getScreenById(Long screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + screenId));
        return ScreenResponse.from(screen);
    }

    @Override
    public SeatLayoutResponse getSeatLayout(Long screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + screenId));

        List<Seat> seats = seatRepository.findByScreenIdOrderByRowNameAscSeatNumberAsc(screenId);
        return SeatLayoutResponse.from(screenId, screen.getName(), seats);
    }
}
