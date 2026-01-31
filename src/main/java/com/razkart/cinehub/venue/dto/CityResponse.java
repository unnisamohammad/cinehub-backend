package com.razkart.cinehub.venue.dto;

import com.razkart.cinehub.venue.entity.City;

public record CityResponse(
        Long id,
        String name,
        String state,
        String country
) {
    public static CityResponse from(City city) {
        return new CityResponse(
                city.getId(),
                city.getName(),
                city.getState(),
                city.getCountry()
        );
    }
}
