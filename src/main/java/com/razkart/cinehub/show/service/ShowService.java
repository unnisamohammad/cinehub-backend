package com.razkart.cinehub.show.service;

import com.razkart.cinehub.show.dto.SeatAvailabilityResponse;
import com.razkart.cinehub.show.dto.ShowRequest;
import com.razkart.cinehub.show.dto.ShowResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShowService {

    ShowResponse createShow(ShowRequest request);

    ShowResponse updateShow(Long id, ShowRequest request);

    ShowResponse getShowById(Long id);

    void deleteShow(Long id);

    void cancelShow(Long id);

    List<ShowResponse> getShowsByEventAndCity(Long eventId, Long cityId, LocalDate date);

    List<ShowResponse> getShowsByVenue(Long venueId, LocalDate date);

    List<ShowResponse> getUpcomingShowsByEvent(Long eventId);

    List<LocalDate> getAvailableDates(Long eventId, Long cityId);

    SeatAvailabilityResponse getSeatAvailability(Long showId);
}
