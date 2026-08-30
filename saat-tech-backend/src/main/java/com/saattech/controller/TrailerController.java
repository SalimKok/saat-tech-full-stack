package com.saattech.controller;

import com.saattech.dto.tmdb.TmdbSaveRequestDto;
import com.saattech.dto.trailer.TrailerResponseDto;
import com.saattech.security.IsAdmin;
import com.saattech.service.TrailerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

    @PostMapping(value = "/{contentId}/trailers/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrailerResponseDto> uploadTrailer(
            @PathVariable Long contentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "type", required = false, defaultValue = "Trailer") String type) {

        TrailerResponseDto trailer = trailerService.uploadAndSaveTrailer(contentId, file, name, type);
        return ResponseEntity.ok(trailer);
    }

    @GetMapping("/{contentId}/trailers/tmdb-preview")
    public ResponseEntity<List<TrailerResponseDto>> previewTmdbTrailers(@PathVariable Long contentId) {
        List<TrailerResponseDto> trailers = trailerService.previewTmdbTrailers(contentId);
        return ResponseEntity.ok(trailers);
    }

    @PostMapping("/{contentId}/trailers/tmdb-save")
    public ResponseEntity<TrailerResponseDto> saveSingleTmdbTrailer(
            @PathVariable Long contentId,
            @RequestBody TmdbSaveRequestDto request) {
        TrailerResponseDto savedTrailer = trailerService.saveSingleTmdbTrailer(contentId, request);
        return ResponseEntity.ok(savedTrailer);
    }

    @PatchMapping("/{contentId}/trailers/{trailerId}")
    public ResponseEntity<TrailerResponseDto> updateTrailerDetails(
            @PathVariable Long contentId,
            @PathVariable Long trailerId,
            @RequestBody Map<String, String> payload) {
        String newName = payload.get("name");
        String newType = payload.get("type");
        TrailerResponseDto updatedTrailer = trailerService.updateTrailerDetails(contentId, trailerId, newName, newType);
        return ResponseEntity.ok(updatedTrailer);
    }

    @DeleteMapping("/{contentId}/trailers/{trailerId}")
    public ResponseEntity<Void> deleteTrailer(
            @PathVariable Long contentId,
            @PathVariable Long trailerId) {
        trailerService.deleteTrailer(contentId, trailerId);
        return ResponseEntity.noContent().build();
    }

}