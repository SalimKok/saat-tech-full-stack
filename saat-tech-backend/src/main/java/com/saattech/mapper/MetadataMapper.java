package com.saattech.mapper;

import com.saattech.dto.metadata.MetadataRequestDto;
import com.saattech.dto.metadata.MetadataResponseDto;
import com.saattech.entity.Metadata;
import org.springframework.stereotype.Component;

@Component
public class MetadataMapper {
    public MetadataResponseDto toDto(Metadata metadata) {
        if (metadata == null) return null;

        MetadataResponseDto dto = new MetadataResponseDto();

        dto.setId(metadata.getId());
        dto.setTitle(metadata.getTitle());
        dto.setPoster(metadata.getPoster());
        dto.setPlot(metadata.getPlot());
        dto.setImdbRating(metadata.getImdbRating());
        dto.setGenre(metadata.getGenre());
        dto.setLanguage(metadata.getLanguage());
        dto.setCountry(metadata.getCountry());
        dto.setReleased(metadata.getReleased());
        dto.setRuntime(metadata.getRuntime());
        dto.setImdbVotes(metadata.getImdbVotes());
        dto.setRated(metadata.getRated());
        dto.setAwards(metadata.getAwards());
        dto.setBoxOffice(metadata.getBoxOffice());
        dto.setMetascore(metadata.getMetascore());
        dto.setImdbID(metadata.getImdbID());
        return dto;
    }
    public Metadata toEntity(MetadataRequestDto requestDto) {
        if (requestDto == null) return null;

        Metadata metadata = new Metadata();

        metadata.setTitle(requestDto.getTitle());
        metadata.setPoster(requestDto.getPoster());
        metadata.setPlot(requestDto.getPlot());
        metadata.setImdbRating(requestDto.getImdbRating());
        metadata.setGenre(requestDto.getGenre());
        metadata.setLanguage(requestDto.getLanguage());
        metadata.setCountry(requestDto.getCountry());
        metadata.setReleased(requestDto.getReleased());
        metadata.setRuntime(requestDto.getRuntime());
        metadata.setImdbVotes(requestDto.getImdbVotes());
        metadata.setRated(requestDto.getRated());
        metadata.setAwards(requestDto.getAwards());
        metadata.setBoxOffice(requestDto.getBoxOffice());
        metadata.setMetascore(requestDto.getMetascore());
        metadata.setImdbID(requestDto.getImdbID());
        return metadata;
    }

    public void updateEntityFromDto(MetadataRequestDto dto, Metadata metadata) {
        if (dto == null || metadata == null) return;

        if (dto.getTitle() != null) metadata.setTitle(dto.getTitle());
        if (dto.getPoster() != null) metadata.setPoster(dto.getPoster());
        if (dto.getPlot() != null) metadata.setPlot(dto.getPlot());
        if (dto.getImdbRating() != null) metadata.setImdbRating(dto.getImdbRating());
        if (dto.getGenre() != null) metadata.setGenre(dto.getGenre());
        if (dto.getLanguage() != null) metadata.setLanguage(dto.getLanguage());
        if (dto.getCountry() != null) metadata.setCountry(dto.getCountry());
        if (dto.getReleased() != null) metadata.setReleased(dto.getReleased());
        if (dto.getRuntime() != null) metadata.setRuntime(dto.getRuntime());
        if (dto.getImdbVotes() != null) metadata.setImdbVotes(dto.getImdbVotes());
    }
}
