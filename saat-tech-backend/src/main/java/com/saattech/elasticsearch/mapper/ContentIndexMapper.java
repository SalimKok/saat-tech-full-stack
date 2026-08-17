package com.saattech.elasticsearch.mapper;

import com.saattech.elasticsearch.model.ContentIndex;
import com.saattech.elasticsearch.service.EmbeddingService;
import com.saattech.entity.Content;
import com.saattech.entity.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentIndexMapper {

    private final EmbeddingService embeddingService;

    public ContentIndex toIndex(Content content) {
        if (content == null)
            return null;
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

        List<Float> plotVector = null;
        if (metadata != null && metadata.getPlot() != null && !metadata.getPlot().trim().isEmpty()) {
            try {
                List<Float> vec = embeddingService.getEmbedding(metadata.getPlot());
                if (vec != null && !vec.isEmpty()) {
                    plotVector = vec;
                }
            } catch (Exception e) {
                log.warn("Could not generate vector embedding for content ID {}: {}", content.getId(), e.getMessage());
            }
        }

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
                .plotVector(plotVector)
                .build();
    }

    private Integer parseRuntimeMinutes(String runtime) {
        if (runtime == null || runtime.trim().isEmpty())
            return null;
        try {
            String digitsOnly = runtime.replaceAll("[^0-9]", "");
            return digitsOnly.isEmpty() ? null : Integer.parseInt(digitsOnly);
        } catch (Exception e) {
            return null;
        }
    }
}