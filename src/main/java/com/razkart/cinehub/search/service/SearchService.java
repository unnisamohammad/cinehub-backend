package com.razkart.cinehub.search.service;

import com.razkart.cinehub.search.dto.SearchRequest;
import com.razkart.cinehub.search.dto.SearchResponse;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    /**
     * Search for events and venues.
     */
    SearchResponse search(SearchRequest request, Pageable pageable);
}
