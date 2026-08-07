package com.saattech.elasticsearch;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ReciprocalRankFusionHelper {
    private static final int RRF_K = 60; //
    public List<ContentIndex> fuseResults(List<SearchHit<ContentIndex>> bm25Hits, List<SearchHit<ContentIndex>> vectorHits) {
        Map<Long, ContentIndex> contentMap = new HashMap<>();
        Map<Long, Double> rrfScores = new HashMap<>();
        Map<Long, Integer> bm25Ranks = new HashMap<>();
        Map<Long, Integer> vectorRanks = new HashMap<>();

        if (bm25Hits != null) {
            for (int rank = 0; rank < bm25Hits.size(); rank++) {
                SearchHit<ContentIndex> hit = bm25Hits.get(rank);
                ContentIndex content = hit.getContent();
                Long id = content.getId();
                contentMap.putIfAbsent(id, content);
                bm25Ranks.put(id, rank + 1);
                double score = 1.0 / (RRF_K + (rank + 1));
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
                double score = 1.0 / (RRF_K + (rank + 1));
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
            String summary = String.format("RRF Fused (BM25 Rank: %s, Vector Rank: %s)",
                    bm25Rank != null ? "#" + bm25Rank : "N/A",
                    vecRank != null ? "#" + vecRank : "N/A");
            MatchExplanationDto explanation = MatchExplanationDto.builder()
                    .totalScore((float) totalRrf)
                    .decisionSummary(summary)
                    .build();
            content.setMatchExplanation(explanation);
            content.setScore((float) totalRrf);
            finalResults.add(content);
        }
        return finalResults;
    }
}