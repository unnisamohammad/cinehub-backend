package com.razkart.cinehub.venue.dto;

import com.razkart.cinehub.venue.entity.Screen;
import com.razkart.cinehub.venue.entity.ScreenType;

public record ScreenResponse(
        Long id,
        String name,
        ScreenType screenType,
        Integer totalSeats,
        Boolean isActive
) {
    public static ScreenResponse from(Screen screen) {
        return new ScreenResponse(
                screen.getId(),
                screen.getName(),
                screen.getScreenType(),
                screen.getTotalSeats(),
                screen.getIsActive()
        );
    }
}
