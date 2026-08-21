package com.saattech.controller;

import  com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentResponseDto;
import com.saattech.enums.CastType;
import com.saattech.security.IsAdmin;
import com.saattech.service.ContentService;
import com.saattech.specification.dto.ContentFilterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
@IsAdmin
public class ContentController {
    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<Page<ContentResponseDto>> getAllContents(ContentFilterDto filterDto, Pageable pageable) {

        Page<ContentResponseDto> contents = contentService.getAllContents(filterDto, pageable);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponseDto> getContentById(@PathVariable Long id) {
        ContentResponseDto content = contentService.getContentById(id);
        return ResponseEntity.ok(content);
    }

    @PostMapping
    public ResponseEntity<ContentResponseDto> saveContent(@Valid @RequestBody ContentRequestDto requestDto){
        ContentResponseDto savedContent = contentService.saveContent(requestDto);
        return new ResponseEntity<>(savedContent, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id){
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{contentId}/casts/{castId}")
    public ResponseEntity<Void> addCastToContent(@PathVariable Long contentId, @PathVariable Long castId, @RequestParam CastType role) {
        contentService.addCastToContent(contentId, castId, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{contentId}/casts/{castId}")
    public ResponseEntity<Void> removeCastFromContent(@PathVariable Long contentId, @PathVariable Long castId) {
        contentService.removeCastFromContent(contentId, castId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponseDto> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody ContentRequestDto requestDto,
            @RequestParam(defaultValue = "false") boolean updateChildren) {
        ContentResponseDto updatedContent = contentService.updateContent(id, requestDto, updateChildren);
        return ResponseEntity.ok(updatedContent);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ContentResponseDto> changeStatus(
            @PathVariable Long id,
            @RequestParam com.saattech.enums.ContentStatus status) {

        ContentResponseDto updatedContent = contentService.changeContentStatus(id, status);
        return ResponseEntity.ok(updatedContent);
    }
}