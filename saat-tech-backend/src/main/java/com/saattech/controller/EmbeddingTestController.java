package com.saattech.controller;

import com.saattech.elasticsearch.service.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/test/embedding")
@RequiredArgsConstructor
@Tag(name = "Embedding & Semantic Search Test API", description = "Model hızı ve anlamsal benzerlik test uç noktaları")
public class EmbeddingTestController {

    private final EmbeddingService embeddingService;

    @GetMapping("/health")
    @Operation(summary = "Model Servis Sağlık Durumu", description = "Ollama modelinin ayakta olup olmadığını kontrol eder")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        boolean available = embeddingService.isAvailable();
        Map<String, Object> response = new HashMap<>();
        response.put("status", available ? "UP" : "DOWN");
        response.put("message", available ? "Embedding modeli hazır ve çalışıyor." : "Ollama servisine ulaşılamadı.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/embed")
    @Operation(summary = "Tekil Vektör Üretimi ve Süre Ölçümü", description = "Verilen metni vektöre çevirir ve süreyi (ms) döner")
    public ResponseEntity<Map<String, Object>> testSingleEmbedding(@RequestBody Map<String, String> request) {
        String text = request.getOrDefault("text", "Örnek arama metni");

        long start = System.currentTimeMillis();
        List<Float> vector = embeddingService.getEmbedding(text);
        long durationMs = System.currentTimeMillis() - start;

        Map<String, Object> response = new HashMap<>();
        response.put("input_text", text);
        response.put("duration_ms", durationMs);
        response.put("vector_dimension", vector.size());
        response.put("vector_preview", vector.size() > 5 ? vector.subList(0, 5) : vector);
        response.put("status", "SUCCESS");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/similarity")
    @Operation(summary = "İki Cümle Arası Anlamsal Benzerlik Testi", description = "İki cümlenin benzerlik yüzdesini ve hızını hesaplar")
    public ResponseEntity<Map<String, Object>> testSimilarity(@RequestBody Map<String, String> request) {
        String text1 = request.getOrDefault("text1", "Uzayda mahsur kalan astronot");
        String text2 = request.getOrDefault("text2", "The Martian filmi mars gezegeninde yalnız kalmayı anlatır");

        long start = System.currentTimeMillis();
        List<Float> vector1 = embeddingService.getEmbedding(text1);
        List<Float> vector2 = embeddingService.getEmbedding(text2);
        double similarity = embeddingService.calculateCosineSimilarity(vector1, vector2);
        long durationMs = System.currentTimeMillis() - start;

        Map<String, Object> response = new HashMap<>();
        response.put("text_1", text1);
        response.put("text_2", text2);
        response.put("cosine_similarity_score", Math.round(similarity * 10000.0) / 10000.0);
        response.put("similarity_percentage", "%" + Math.round(similarity * 1000.0) / 10.0);
        response.put("total_duration_ms", durationMs);

        return ResponseEntity.ok(response);
    }
}
