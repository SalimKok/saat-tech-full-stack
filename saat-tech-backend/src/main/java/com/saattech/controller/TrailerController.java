package com.saattech.controller;

import com.saattech.dto.response.TrailerResponseDto;
import com.saattech.security.IsAdmin;
import com.saattech.service.TrailerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contents-trailer")
@RequiredArgsConstructor
@IsAdmin
public class TrailerController {

    private final TrailerService trailerService;

    @GetMapping("/{contentId}/trailers")
    public ResponseEntity<List<TrailerResponseDto>> getTrailers(@PathVariable Long contentId) {
        List<TrailerResponseDto> trailers = trailerService.getTrailersByContentId(contentId);
        return ResponseEntity.ok(trailers);
    }

    @PostMapping("/{contentId}/trailers/fetch")
    public ResponseEntity<List<TrailerResponseDto>> fetchTrailersFromTmdb(@PathVariable Long contentId) {
        List<TrailerResponseDto> trailers = trailerService.fetchAndSaveTrailers(contentId);
        return ResponseEntity.ok(trailers);
    }

    @DeleteMapping("/{contentId}/trailers")
    public ResponseEntity<Void> deleteTrailers(@PathVariable Long contentId) {
        trailerService.deleteTrailersByContentId(contentId);
        return ResponseEntity.noContent().build();
    }
}