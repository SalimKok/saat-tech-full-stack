package com.saattech.elasticsearch.service.impl;

import com.saattech.elasticsearch.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaEmbeddingServiceImpl implements EmbeddingService {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${app.embedding.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${app.embedding.model:all-minilm}")
    private String modelName;

    @Override
    public List<Float> getEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            RestTemplate restTemplate = restTemplateBuilder
                    .connectTimeout(Duration.ofSeconds(10))
                    .readTimeout(Duration.ofSeconds(20))
                    .build();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("prompt", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String endpoint = ollamaUrl + "/api/embeddings";
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Double> rawEmbedding = (List<Double>) response.getBody().get("embedding");
                if (rawEmbedding != null) {
                    List<Float> floatEmbedding = new ArrayList<>(rawEmbedding.size());
                    for (Double val : rawEmbedding) {
                        floatEmbedding.add(val.floatValue());
                    }
                    return floatEmbedding;
                }
            }
        } catch (Exception e) {
                log.error("Error while creating embedding: {}", e.getMessage());
                throw new RuntimeException("The embedding service was unavailable: " + e.getMessage(), e);
        }

        return Collections.emptyList();
    }


    @EventListener(ApplicationReadyEvent.class)
    public void warmupModel() {
        log.info("Warming up Ollama embedding model in background...");
        try {
            getEmbedding("system warmup initialization query");
            log.info("Ollama embedding model warmed up successfully and ready for search!");
        } catch (Exception e) {
            log.warn("Ollama warmup skipped: {}", e.getMessage());
        }
    }
}