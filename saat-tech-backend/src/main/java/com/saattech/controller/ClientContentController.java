package com.saattech.controller;

import com.saattech.dto.content.ContentResponseDto;
import com.saattech.security.IsAuthenticated;
import com.saattech.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/contents")
@RequiredArgsConstructor
@IsAuthenticated
public class ClientContentController {

    private final ContentService contentService;

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponseDto> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }
}
