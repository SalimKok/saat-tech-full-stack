package com.saattech.elasticsearch;
import com.saattech.elasticsearch.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchExplanationHelper {

    private final EmbeddingService embeddingService;

    public List<ContentIndex> processSearchHits(SearchHits<ContentIndex> searchHits, List<String> passedFilters,  List<Float> queryVector) {
        return searchHits.getSearchHits().stream()
                .map(hit -> mapToExplainedItem(hit, passedFilters, queryVector))
                .collect(Collectors.toList());
    }

    private ContentIndex mapToExplainedItem(SearchHit<ContentIndex> hit, List<String> passedFilters,  List<Float> queryVector) {
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

        Double semanticScore = null;
        String semanticPercentage = null;
        boolean isSemantic = false;
        if (queryVector != null && !queryVector.isEmpty() && item.getPlotVector() != null && !item.getPlotVector().isEmpty()) {
            semanticScore = embeddingService.calculateCosineSimilarity(queryVector, item.getPlotVector());
            semanticScore = Math.round(semanticScore * 10000.0) / 10000.0;
            semanticPercentage = String.format("%%% .1f", Math.max(0.0, semanticScore * 100));
            isSemantic = semanticScore >= 0.25;
            if (isSemantic) {
                matchedFields.add("semantic_plot");
            }
        }

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