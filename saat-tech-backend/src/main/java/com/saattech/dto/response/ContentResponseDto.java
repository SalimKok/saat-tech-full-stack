package com.saattech.dto.response;

import com.saattech.enums.ContentType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContentResponseDto {
    private Long id;
    private Integer seasonNo;
    private Integer episodeNo;
    private ContentType contentType;
    private LocalDateTime createdAt;
    private List<ContentResponseDto> subContents;
    private List<ContentCastResponseDto> casts;
    private MetadataResponseDto metadata;
}
