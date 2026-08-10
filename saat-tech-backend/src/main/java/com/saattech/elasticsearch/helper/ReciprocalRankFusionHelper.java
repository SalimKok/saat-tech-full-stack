package com.saattech.elasticsearch.helper;
import com.saattech.elasticsearch.dto.MatchExplanationDto;
import com.saattech.elasticsearch.model.ContentIndex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ReciprocalRankFusionHelper {
    @Value("${app.search.rrf.k:60}")
    private int rrfK;
    @Value("${app.search.rrf.weight.bm25:0.5}")
    private double bm25Weight;
    @Value("${app.search.rrf.weight.vector:1.5}")
    private double vectorWeight;

    public List<ContentIndex> fuseResults(List<SearchHit<ContentIndex>> bm25Hits, List<SearchHit<ContentIndex>> vectorHits) {
        Map<Long, ContentIndex> contentMap = new HashMap<>();
        Map<Long, Double> rrfScores = new HashMap<>();
        Map<Long, Integer> bm25Ranks = new HashMap<>();
        Map<Long, Integer> vectorRanks = new HashMap<>();

        Map<Long, Map<String, List<String>>> highlightMap = new HashMap<>();
        Map<Long, Float> rawBm25Scores = new HashMap<>();
        Map<Long, Float> rawSemanticScores = new HashMap<>();

        if (bm25Hits != null) {
            for (int rank = 0; rank < bm25Hits.size(); rank++) {
                SearchHit<ContentIndex> hit = bm25Hits.get(rank);
                ContentIndex content = hit.getContent();
                Long id = content.getId();
                contentMap.putIfAbsent(id, content);
                bm25Ranks.put(id, rank + 1);

                if (hit.getScore() > 0) rawBm25Scores.put(id, hit.getScore());
                if (hit.getHighlightFields() != null && !hit.getHighlightFields().isEmpty()) {
                    highlightMap.put(id, hit.getHighlightFields());
                }
                double score = (1.0 / (rrfK + (rank + 1))) * bm25Weight;
                rrfScores.put(id, rrfScores.getOrDefault(id, 0.0) + score);
            }
        }
        if (vectorHits != null) {
            for (int rank = 0; rank < vectorHits.size(); rank++) {
                SearchHit<ContentIndex> hit = vectorHits.get(rank);
                ContentIndex content = hit.getContent();
                Long id = content.getId();
                contentMap.putIfAbsent(id, content);
                vectorRanks.put(id, rank + 1);

                if (hit.getScore() > 0) rawSemanticScores.put(id, hit.getScore());
                double score = (1.0 / (rrfK + (rank + 1))) * vectorWeight;
                rrfScores.put(id, rrfScores.getOrDefault(id, 0.0) + score);
            }
        }
        List<Map.Entry<Long, Double>> sortedEntries = new ArrayList<>(rrfScores.entrySet());
        sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        List<ContentIndex> finalResults = new ArrayList<>();

        for (Map.Entry<Long, Double> entry : sortedEntries) {
            Long id = entry.getKey();
            ContentIndex content = contentMap.get(id);
            double totalRrf = entry.getValue();
            Integer bm25Rank = bm25Ranks.get(id);
            Integer vecRank = vectorRanks.get(id);

            Float bScore = rawBm25Scores.getOrDefault(id, 0.0f);
            Float sScore = rawSemanticScores.getOrDefault(id, 0.0f);


            String summary;
            if (bm25Rank != null && vecRank != null) {
                summary = String.format("BM25 & Semantic Match (BM25: #%d, Vector: #%d)", bm25Rank, vecRank);
            } else if (vecRank != null) {
                summary = String.format("Pure Semantic Match (#%d)", vecRank);
            } else {
                summary = String.format("Keyword BM25 Match (#%d)", bm25Rank);
            }

            Map<String, List<String>> highlights = highlightMap.getOrDefault(id, new HashMap<>());

            MatchExplanationDto explanation = MatchExplanationDto.builder()
                    .totalScore((float) totalRrf)
                    .bm25Score(bScore)
                    .semanticSimilarityScore(sScore.doubleValue())
                    .decisionSummary(summary)
                    .highlightedSnippets(highlights)
                    .build();

            content.setMatchExplanation(explanation);
            content.setScore((float) totalRrf);
            finalResults.add(content);
        }
        return finalResults;
    }
}