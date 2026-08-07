package com.saattech.elasticsearch;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SearchExplanationHelper {

    public List<ContentIndex> processSearchHits(SearchHits<ContentIndex> searchHits, List<String> passedFilters) {
        return searchHits.getSearchHits().stream()
                .map(hit -> mapToExplainedItem(hit, passedFilters))
                .collect(Collectors.toList());
    }

    private ContentIndex mapToExplainedItem(SearchHit<ContentIndex> hit, List<String> passedFilters) {
        ContentIndex item = hit.getContent();
        float totalScore = hit.getScore() > 0 ? hit.getScore() : 1.0f;
        item.setScore(totalScore);

        List<String> matchedFields = new ArrayList<>();
        Map<String, List<String>> highlights = new HashMap<>();
        Map<String, Integer> frequencies = new HashMap<>();

        hit.getHighlightFields().forEach((field, snippets) -> {
            if (snippets != null && !snippets.isEmpty()) {
                matchedFields.add(field);
                highlights.put(field, snippets);
                int count = 0;
                for (String s : snippets) {
                    count += (s.split("<mark", -1).length - 1);
                }
                frequencies.put(field, Math.max(count, 1));
            }
        });

        float titlePts = matchedFields.contains("title") ? totalScore * 0.5f : 0f;
        float plotPts  = matchedFields.contains("plot") ? totalScore * 0.35f : 0f;
        float castPts  = matchedFields.contains("castNames") ? totalScore * 0.15f : 0f;
        float genrePts = matchedFields.contains("genre") ? totalScore * 0.10f : 0f;

        MatchExplanationDto explanation = MatchExplanationDto.builder()
                .totalScore(totalScore)
                .titleScore(titlePts)
                .plotScore(plotPts)
                .castScore(castPts)
                .genreScore(genrePts)
                .matchedFields(matchedFields)
                .highlightedSnippets(highlights)
                .termFrequencies(frequencies)
                .passedFilters(passedFilters)
                .build();

        item.setMatchExplanation(explanation);
        return item;
    }
}