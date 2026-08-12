package com.saattech.elasticsearch.service.impl;


import com.saattech.elasticsearch.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaEmbeddingServiceImpl implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    @Override
    public List<Float> getEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {

            float[] vector = embeddingModel.embed(text);

            if (vector != null) {
                List<Float> floatEmbedding = new ArrayList<>(vector.length);
                for (float v : vector) {
                    floatEmbedding.add(v);
                }
                return floatEmbedding;
            }
        } catch (Exception e) {
            log.error("Error while creating embedding via Spring AI: {}", e.getMessage());
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