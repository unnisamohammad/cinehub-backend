package com.razkart.cinehub.venue.dto;

import com.razkart.cinehub.venue.entity.Venue;
import com.razkart.cinehub.venue.entity.VenueStatus;

import java.math.BigDecimal;
import java.util.List;

public record VenueResponse(
        Long id,
        String name,
        CityResponse city,
        String address,
        String landmark,
        BigDecimal latitude,
        BigDecimal longitude,
        String contactPhone,
        String contactEmail,
        String facilities,
        VenueStatus status,
        List<ScreenResponse> screens
) {
    public static VenueResponse from(Venue venue) {
        List<ScreenResponse> screenList = venue.getScreens().stream()
                .map(ScreenResponse::from)
                .toList();

        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                CityResponse.from(venue.getCity()),
                venue.getAddress(),
                venue.getLandmark(),
                venue.getLatitude(),
                venue.getLongitude(),
                venue.getContactPhone(),
                venue.getContactEmail(),
                venue.getFacilities(),
                venue.getStatus(),
                screenList
        );
    }

    public static VenueResponse fromWithoutScreens(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                CityResponse.from(venue.getCity()),
                venue.getAddress(),
                venue.getLandmark(),
                venue.getLatitude(),
                venue.getLongitude(),
                venue.getContactPhone(),
                venue.getContactEmail(),
                venue.getFacilities(),
                venue.getStatus(),
                null
        );
    }
}
