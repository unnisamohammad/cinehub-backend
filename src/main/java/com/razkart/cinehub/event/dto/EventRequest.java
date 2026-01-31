package com.razkart.cinehub.event.dto;

import com.razkart.cinehub.event.entity.EventCategory;
import com.razkart.cinehub.event.entity.EventStatus;
import com.razkart.cinehub.event.entity.Rating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record EventRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        String description,

        @NotNull(message = "Category is required")
        EventCategory category,

        String language,

        Integer durationMinutes,

        Rating rating,

        String genre,

        String posterUrl,

        String bannerUrl,

        String trailerUrl,

        LocalDate releaseDate,

        EventStatus status,

        List<CastRequest> cast
) {
    public record CastRequest(
            @NotBlank(message = "Person name is required")
            String personName,
            @NotNull(message = "Role type is required")
            String roleType,
            String characterName,
            String imageUrl,
            Integer displayOrder
    ) {}
}
