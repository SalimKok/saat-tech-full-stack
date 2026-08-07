package com.saattech.controller;

import com.saattech.elasticsearch.ContentIndex;
import com.saattech.elasticsearch.service.ContentSearchService;
import com.saattech.specification.dto.ContentFilterDto;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentSearchController {

    private final ContentSearchService contentSearchService;

    @GetMapping("/search")
    public ResponseEntity<Page<ContentIndex>> searchContents(
            @RequestParam(required = false) String query,
            @ParameterObject ContentFilterDto filterDto,
            @ParameterObject Pageable pageable) {

        Page<ContentIndex> searchResults = contentSearchService.search(query, filterDto, pageable);
        return ResponseEntity.ok(searchResults);
    }


    @PostMapping("/sync-elasticsearch")
    public ResponseEntity<String> syncElasticsearch() {
        contentSearchService.syncAllContents();
        return ResponseEntity.ok("All contents synchronized to Elasticsearch successfully!");
    }
}
