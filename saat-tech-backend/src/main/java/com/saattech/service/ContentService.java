package com.saattech.service;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentResponseDto;
import com.saattech.enums.CastType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContentService {

    Page<ContentResponseDto> getAllContents(com.saattech.specification.dto.ContentFilterDto filterDto,Pageable pageable);

    ContentResponseDto saveContent(ContentRequestDto requestDto);

    void deleteContent(Long id);

    void addCastToContent(Long contentId, Long castId, CastType role);

    void removeCastFromContent(Long contentId, Long castId);

    ContentResponseDto updateContent(Long id, ContentRequestDto requestDto, boolean updateChildren);

    ContentResponseDto getContentById(Long id);

    ContentResponseDto changeContentStatus(Long id, com.saattech.enums.ContentStatus newStatus);

}
