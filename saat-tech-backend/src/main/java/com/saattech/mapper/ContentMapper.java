package com.saattech.mapper;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentCastResponseDto;
import com.saattech.dto.response.ContentResponseDto;
import com.saattech.dto.response.LicenseResponseDto;
import com.saattech.entity.Cast;
import com.saattech.entity.Content;
import com.saattech.entity.ContentCast;
import com.saattech.enums.CastType;
import com.saattech.enums.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContentMapper {

    private final CastMapper castMapper;
    private final MetadataMapper metadataMapper;
    private final LicenseMapper licenseMapper;

    public ContentResponseDto toDto(Content content) {
        if (content == null) {
            return null;
        }

        ContentResponseDto dto = new ContentResponseDto();
        dto.setId(content.getId());
        dto.setSeasonNo(content.getSeasonNo());
        dto.setEpisodeNo(content.getEpisodeNo());
        dto.setContentType(content.getContentType());
        dto.setCreatedAt(content.getCreatedAt());
        dto.setStatus(content.getStatus());

        if (content.getMetadata() != null) {
            dto.setMetadata(metadataMapper.toDto(content.getMetadata()));
        }

        if (content.getSubContents() != null && !content.getSubContents().isEmpty()) {
            List<ContentResponseDto> subDtoList = content.getSubContents().stream()
                    .filter(child -> child.getStatus()  != ContentStatus.DELETED)
                    .map(this::toDto)
                    .collect(Collectors.toList());

            dto.setSubContents(subDtoList);
        }

        if (content.getCastMembers() != null && !content.getCastMembers().isEmpty()) {
            List<ContentCastResponseDto> contentCastDtoList = content.getCastMembers().stream()
                    .map(contentCast -> {
                        ContentCastResponseDto ccDto = new ContentCastResponseDto();
                        ccDto.setId(contentCast.getId());
                        ccDto.setRole(contentCast.getRole());
                        ccDto.setCast(castMapper.toDto(contentCast.getCast()));
                        return ccDto;
                    })
                    .collect(Collectors.toList());

            dto.setCasts(contentCastDtoList);
        }

        if (content.getLicenses() != null && !content.getLicenses().isEmpty()) {
            List<LicenseResponseDto> licenseDtoList = content.getLicenses().stream()
                    .map(licenseMapper::toDto)
                    .collect(Collectors.toList());
            dto.setLicenses(licenseDtoList);
        }

        return dto;
    }

    public Content toEntity(ContentRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        Content content = new Content();
        content.setSeasonNo(requestDto.getSeasonNo());
        content.setEpisodeNo(requestDto.getEpisodeNo());
        content.setContentType(requestDto.getContentType());

        return content;
    }

    public void updateEntityFromDto(ContentRequestDto dto, Content content) {
        if (dto == null || content == null)
            return;

        if (dto.getSeasonNo() != null)
            content.setSeasonNo(dto.getSeasonNo());
        if (dto.getEpisodeNo() != null)
            content.setEpisodeNo(dto.getEpisodeNo());
        if (dto.getContentType() != null)
            content.setContentType(dto.getContentType());

    }

    public ContentCast toContentCast(Content content, Cast cast, CastType role) {
        if (content == null || cast == null || role == null)
            return null;

        ContentCast contentCast = new ContentCast();
        contentCast.setContent(content);
        contentCast.setCast(cast);
        contentCast.setRole(role);
        return contentCast;
    }
}
