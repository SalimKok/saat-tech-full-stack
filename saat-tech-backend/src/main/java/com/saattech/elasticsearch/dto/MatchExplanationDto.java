package com.saattech.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchExplanationDto {
    private Float totalScore;
    private Float bm25Score;

    private List<String> matchedFields;
    private Map<String, List<String>> highlightedSnippets;
    private Map<String, Integer> termFrequencies;
    private List<String> passedFilters;
    private String decisionSummary;

    private Double semanticSimilarityScore;
    private String semanticSimilarityPercentage;
    private Boolean isSemanticMatch;
}