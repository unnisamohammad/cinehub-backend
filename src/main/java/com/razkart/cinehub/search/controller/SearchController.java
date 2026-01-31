package com.razkart.cinehub.search.controller;

import com.razkart.cinehub.common.dto.ApiResponse;
import com.razkart.cinehub.search.dto.SearchRequest;
import com.razkart.cinehub.search.dto.SearchResponse;
import com.razkart.cinehub.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search APIs")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Search events and venues")
    public ResponseEntity<ApiResponse<SearchResponse>> search(
            @RequestParam String q,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String date,
            @PageableDefault(size = 20) Pageable pageable) {

        SearchRequest request = new SearchRequest(q, cityId, category, language, date);
        SearchResponse response = searchService.search(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
