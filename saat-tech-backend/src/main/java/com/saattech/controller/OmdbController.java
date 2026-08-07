package com.saattech.controller;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.BulkImportResponseDto;
import com.saattech.service.ContentService;
import com.saattech.service.OmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/omdb")
@RequiredArgsConstructor
@Slf4j
public class OmdbController {
    private final OmdbService omdbService;
    private final ContentService contentService;

    @GetMapping("/preview")
    public ResponseEntity<ContentRequestDto> previewFromOmdb(@RequestParam String imdbId) {
        ContentRequestDto requestDto = omdbService.fetchFromOmdb(imdbId);
        return new ResponseEntity<>(requestDto, HttpStatus.OK);
    }

    @PostMapping("/bulk-import")
    public ResponseEntity<BulkImportResponseDto> bulkImportFromOmdb(@RequestBody List<String> imdbIds) {
        BulkImportResponseDto response = omdbService.bulkImportFromOmdb(imdbIds);
        return ResponseEntity.ok(response);
    }
}
