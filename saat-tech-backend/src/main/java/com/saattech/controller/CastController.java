package com.saattech.controller;

import com.saattech.dto.request.CastRequestDto;
import com.saattech.dto.response.CastResponseDto;
import com.saattech.service.CastService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/casts")
@RequiredArgsConstructor
public class CastController {

    private final CastService castService;


    @GetMapping
    public ResponseEntity<Page<CastResponseDto>> getAllCasts(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        Page<CastResponseDto> response = castService.getAllCasts(name, pageable);
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
}

