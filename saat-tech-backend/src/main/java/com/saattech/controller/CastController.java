package com.saattech.controller;

import com.saattech.dto.cast.CastContentDto;
import com.saattech.dto.cast.CastRequestDto;
import com.saattech.dto.cast.CastResponseDto;
import com.saattech.security.IsAdmin;
import com.saattech.service.CastService;
import com.saattech.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/casts")
@RequiredArgsConstructor
@IsAdmin
public class CastController {

    private final CastService castService;
    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<Page<CastResponseDto>> getAllCasts(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        Page<CastResponseDto> response = castService.getAllCasts(name, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CastResponseDto> getCastById(@PathVariable Long id) {
        CastResponseDto response = castService.getCastById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}/contents")
    public ResponseEntity<Page<CastContentDto>> getCastContents(@PathVariable Long id, Pageable pageable) {
        Page<CastContentDto> response = castService.getCastContents(id, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CastResponseDto> saveCast(@Valid @RequestBody CastRequestDto requestDto) {
        CastResponseDto savedCast = castService.saveCast(requestDto);
        return new ResponseEntity<>(savedCast, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCast(@PathVariable Long id){
        castService.deleteCast(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CastResponseDto> updateCast(@PathVariable Long id,@Valid @RequestBody CastRequestDto requestDto) {
        CastResponseDto updatedCast = castService.updateCast(id, requestDto);
        return ResponseEntity.ok(updatedCast);
    }

    @PostMapping("/upload-poster")
    public ResponseEntity<Map<String, String>> uploadPoster(@RequestParam("file") MultipartFile file) {
        String fileUrl = storageService.store(file);

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);

        return ResponseEntity.ok(response);
    }
}

