package com.razkart.cinehub.show.dto;

import com.razkart.cinehub.show.entity.Show;
import com.razkart.cinehub.show.entity.ShowPricing;
import com.razkart.cinehub.show.entity.ShowStatus;
import com.razkart.cinehub.venue.entity.SeatType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ShowResponse(
        Long id,
        Long eventId,
        String eventTitle,
        Long screenId,
        String screenName,
        Long venueId,
        String venueName,
        LocalDate showDate,
        LocalTime startTime,
        LocalTime endTime,
        ShowStatus status,
        boolean isBookable,
        List<PricingResponse> pricing
) {
    public record PricingResponse(
            SeatType seatType,
            BigDecimal price
    ) {
        public static PricingResponse from(ShowPricing pricing) {
            return new PricingResponse(pricing.getSeatType(), pricing.getPrice());
        }
    }

    public static ShowResponse from(Show show) {
        List<PricingResponse> pricingList = show.getPricing().stream()
                .map(PricingResponse::from)
                .toList();

        return new ShowResponse(
                show.getId(),
                show.getEvent().getId(),
                show.getEvent().getTitle(),
                show.getScreen().getId(),
                show.getScreen().getName(),
                show.getScreen().getVenue().getId(),
                show.getScreen().getVenue().getName(),
                show.getShowDate(),
                show.getStartTime(),
                show.getEndTime(),
                show.getStatus(),
                show.isBookable(),
                pricingList
        );
    }
}
