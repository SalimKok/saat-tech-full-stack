package com.saattech.mapper;

import com.saattech.dto.response.TrailerResponseDto;
import com.saattech.entity.Trailer;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrailerMapper {

    private static final String YOUTUBE_EMBED_BASE = "https://www.youtube.com/embed/";
    private static final String YOUTUBE_THUMBNAIL_BASE = "https://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_SUFFIX = "/hqdefault.jpg";

    public TrailerResponseDto toDto(Trailer trailer) {
        if (trailer == null) {
            return null;
        }

        TrailerResponseDto dto = new TrailerResponseDto();
        dto.setId(trailer.getId());
        dto.setName(trailer.getName());
        dto.setYoutubeKey(trailer.getYoutubeKey());
        dto.setSite(trailer.getSite());
        dto.setType(trailer.getType());
        dto.setSize(trailer.getSize());
        dto.setLanguage(trailer.getLanguage());

        if (trailer.getYoutubeKey() != null) {
            dto.setYoutubeEmbedUrl(YOUTUBE_EMBED_BASE + trailer.getYoutubeKey());
            dto.setYoutubeThumbnailUrl(YOUTUBE_THUMBNAIL_BASE + trailer.getYoutubeKey() + YOUTUBE_THUMBNAIL_SUFFIX);
        }

        return dto;
    }

    public List<TrailerResponseDto> toDtoList(List<Trailer> trailers) {
        if (trailers == null || trailers.isEmpty()) {
            return Collections.emptyList();
        }
        return trailers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
