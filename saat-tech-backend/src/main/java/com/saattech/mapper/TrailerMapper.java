package com.saattech.mapper;

import com.saattech.dto.tmdb.TmdbSaveRequestDto;
import com.saattech.dto.trailer.TrailerResponseDto;
import com.saattech.entity.Trailer;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.saattech.dto.tmdb.TmdbVideoResponseDto;
import com.saattech.entity.Content;


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
        dto.setFileUrl(trailer.getFileUrl());

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

    public Trailer toEntity(TmdbVideoResponseDto.TmdbVideoResult result, Content content) {
        if (result == null) {
            return null;
        }

        Trailer trailer = new Trailer();
        trailer.setName(result.getName());
        trailer.setYoutubeKey(result.getKey());
        trailer.setSite(result.getSite());
        trailer.setType(result.getType());
        trailer.setSize(result.getSize());
        trailer.setLanguage(result.getLanguage());
        trailer.setContent(content);

        return trailer;
    }

    public Trailer toEntity(TmdbSaveRequestDto request, Content content) {
        if (request == null) {
            return null;
        }
        Trailer trailer = new Trailer();
        trailer.setContent(content);
        trailer.setName(request.getName());
        trailer.setYoutubeKey(request.getYoutubeKey());
        trailer.setSite(request.getSite());
        trailer.setType(request.getType());
        trailer.setSize(request.getSize());
        trailer.setLanguage(request.getLanguage());
        return trailer;
    }

    public Trailer toEntityForLocalUpload(Content content, String name, String type, String fileUrl, long size) {
        Trailer trailer = new Trailer();
        trailer.setContent(content);
        trailer.setName(name);
        trailer.setType(type);
        trailer.setFileUrl(fileUrl);
        trailer.setSize((int) size);
        trailer.setSite("Local");
        trailer.setYoutubeKey("");
        return trailer;
    }

}
