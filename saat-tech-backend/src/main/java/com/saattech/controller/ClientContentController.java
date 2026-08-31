package com.saattech.controller;

import com.saattech.dto.content.ContentResponseDto;
import com.saattech.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/contents")
@RequiredArgsConstructor
public class ClientContentController {

    private final ContentService contentService;

    // Bu uç noktada @IsAdmin YOKTUR. Sisteme giriş yapan müşteriler (veya herkes) filmin detaylarını/fragmanlarını görebilir.
    @GetMapping("/{id}")
    public ResponseEntity<ContentResponseDto> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }
}
