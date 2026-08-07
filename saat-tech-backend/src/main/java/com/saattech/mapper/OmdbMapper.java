package com.saattech.mapper;

import com.saattech.dto.omdb.OmdbResponseDto;
import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.request.MetadataRequestDto;
import com.saattech.enums.ContentType;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
@Component
public class OmdbMapper {

    public ContentRequestDto toContentRequestDto(OmdbResponseDto omdbDto) {

        if (omdbDto == null || !"True".equalsIgnoreCase(omdbDto.getResponse())) {
            return null;
        }
        ContentRequestDto contentDto = new ContentRequestDto();

        if ("series".equalsIgnoreCase(omdbDto.getType())) {
            contentDto.setContentType(ContentType.SERIES);
        } else if ("episode".equalsIgnoreCase(omdbDto.getType())) {
            contentDto.setContentType(ContentType.EPISODE);
        } else {
            contentDto.setContentType(ContentType.MOVIE);
        }
        if (omdbDto.getSeason() != null && !omdbDto.getSeason().equalsIgnoreCase("N/A")) {
            try { contentDto.setSeasonNo(Integer.parseInt(omdbDto.getSeason())); } catch(Exception e) {}
        }
        if (omdbDto.getEpisode() != null && !omdbDto.getEpisode().equalsIgnoreCase("N/A")) {
            try { contentDto.setEpisodeNo(Integer.parseInt(omdbDto.getEpisode())); } catch(Exception e) {}
        }

        MetadataRequestDto metaDto = new MetadataRequestDto();
        metaDto.setTitle(omdbDto.getTitle());
        metaDto.setPoster(omdbDto.getPoster());
        metaDto.setGenre(omdbDto.getGenre());
        metaDto.setPlot(omdbDto.getPlot());
        metaDto.setLanguage(omdbDto.getLanguage());
        metaDto.setCountry(omdbDto.getCountry());
        metaDto.setRuntime(omdbDto.getRuntime());
        metaDto.setImdbVotes(omdbDto.getImdbVotes());
        metaDto.setImdbID(omdbDto.getImdbID());
        metaDto.setRated(omdbDto.getRated());
        metaDto.setAwards(omdbDto.getAwards());
        metaDto.setMetascore(omdbDto.getMetascore());
        metaDto.setBoxOffice(omdbDto.getBoxOffice());
        metaDto.setImdbRating(parseRating(omdbDto.getImdbRating()));
        metaDto.setReleased(parseDate(omdbDto.getReleased()));
        contentDto.setMetadata(metaDto);
        return contentDto;
    }

    private Double parseRating(String ratingStr) {
        if (ratingStr == null || "N/A".equalsIgnoreCase(ratingStr)) return null;
        try {
            return Double.parseDouble(ratingStr);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || "N/A".equalsIgnoreCase(dateStr)) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }
}