package com.razkart.cinehub.search.dto;

public record SearchRequest(
        String query,
        Long cityId,
        String category,
        String language,
        String date
) {}
