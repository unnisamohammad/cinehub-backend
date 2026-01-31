package com.razkart.cinehub.event.dto;

import com.razkart.cinehub.event.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record EventResponse(
        Long id,
        String title,
        String description,
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
        BigDecimal avgRating,
        Integer totalReviews,
        List<CastResponse> cast,
        LocalDateTime createdAt
) {
    public record CastResponse(
            Long id,
            String personName,
            RoleType roleType,
            String characterName,
            String imageUrl,
            Integer displayOrder
    ) {
        public static CastResponse from(Cast cast) {
            return new CastResponse(
                    cast.getId(),
                    cast.getPersonName(),
                    cast.getRoleType(),
                    cast.getCharacterName(),
                    cast.getImageUrl(),
                    cast.getDisplayOrder()
            );
        }
    }

    public static EventResponse from(Event event) {
        List<CastResponse> castList = event.getCastMembers().stream()
                .map(CastResponse::from)
                .toList();

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getLanguage(),
                event.getDurationMinutes(),
                event.getRating(),
                event.getGenre(),
                event.getPosterUrl(),
                event.getBannerUrl(),
                event.getTrailerUrl(),
                event.getReleaseDate(),
                event.getStatus(),
                event.getAvgRating(),
                event.getTotalReviews(),
                castList,
                event.getCreatedAt()
        );
    }
}
