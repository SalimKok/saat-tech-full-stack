package com.saattech.dto.content;

import com.saattech.dto.contentcast.ContentCastResponseDto;
import com.saattech.dto.license.LicenseResponseDto;
import com.saattech.dto.metadata.MetadataResponseDto;
import com.saattech.dto.trailer.TrailerResponseDto;
import com.saattech.enums.ContentStatus;
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
    private ContentStatus status;
    private List<LicenseResponseDto> licenses;
    private List<TrailerResponseDto> trailers;
}
