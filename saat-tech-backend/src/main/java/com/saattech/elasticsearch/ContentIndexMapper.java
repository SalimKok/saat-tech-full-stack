package com.saattech.elasticsearch;

import com.saattech.entity.Content;
import com.saattech.entity.Metadata;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContentIndexMapper {
    public ContentIndex toIndex(Content content) {
        if (content == null) return null;
        Metadata metadata = content.getMetadata();
        List<String> castNames = new ArrayList<>();
        if (content.getCastMembers() != null) {
            castNames = content.getCastMembers().stream()
                    .filter(c -> c.getCast() != null && c.getCast().getName() != null)
                    .map(c -> c.getCast().getName())
                    .collect(Collectors.toList());
        }
        Integer year = null;
        if (metadata != null && metadata.getReleased() != null) {
            year = metadata.getReleased().getYear();
        }
        Integer runtimeMinutes = parseRuntimeMinutes(metadata != null ? metadata.getRuntime() : null);
        return ContentIndex.builder()
                .id(content.getId())
                .contentType(content.getContentType())
                .status(content.getStatus())
                .seasonNo(content.getSeasonNo())
                .episodeNo(content.getEpisodeNo())
                .title(metadata != null ? metadata.getTitle() : null)
                .plot(metadata != null ? metadata.getPlot() : null)
                .genre(metadata != null ? metadata.getGenre() : null)
                .imdbRating(metadata != null ? metadata.getImdbRating() : null)
                .year(year)
                .poster(metadata != null ? metadata.getPoster() : null)
                .runtimeMinutes(runtimeMinutes)
                .castNames(castNames)
                .build();
    }
    private Integer parseRuntimeMinutes(String runtime) {
        if (runtime == null || runtime.trim().isEmpty()) return null;
        try {
            String digitsOnly = runtime.replaceAll("[^0-9]", "");
            return digitsOnly.isEmpty() ? null : Integer.parseInt(digitsOnly);
        } catch (Exception e) {
            return null;
        }
    }
}