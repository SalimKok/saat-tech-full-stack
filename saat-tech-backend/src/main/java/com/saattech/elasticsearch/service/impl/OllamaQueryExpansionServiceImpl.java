package com.saattech.elasticsearch.service.impl;

import com.saattech.config.ExpansionProperties;
import com.saattech.config.OllamaProperties;
import com.saattech.elasticsearch.service.QueryExpansionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaQueryExpansionServiceImpl implements QueryExpansionService {
    private final ExpansionProperties expansionProperties;
    private final RestTemplateBuilder restTemplateBuilder;
    private final OllamaProperties ollamaProperties;

    @Override
    public boolean needsExpansion(String query) {
        if (!expansionProperties.isEnabled() || query == null || query.isBlank())
            return false;
        String[] words = query.trim().split("\\s+");
        return words.length >= expansionProperties.getMinWords();
    }

    @Override
    public String expand(String query) {
        try {
            RestTemplate restTemplate = restTemplateBuilder
                    .connectTimeout(Duration.ofSeconds(ollamaProperties.getTimeout().getConnect()))
                    .readTimeout(Duration.ofSeconds(ollamaProperties.getTimeout().getRead()))
                    .build();
            Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", expansionProperties.getModel());
                requestBody.put("prompt", buildPrompt(query));
                requestBody.put("stream", false);
                requestBody.put("options", Map.of("temperature", 0));

            HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(
                    ollamaProperties.getUrl() + "/api/generate", entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String expanded = (String) response.getBody().get("response");

                if (expanded != null) {

                    if (expanded.contains("Keywords:")) {
                        expanded = expanded.substring(expanded.lastIndexOf("Keywords:") + 9);
                    }

                    expanded = expanded.replaceAll("(?i)it sounds like.*|here are.*", "").trim();

                    if (isValidExpansion(expanded)) {
                        log.info("==> [RAG] Query expanded: [{}] → [{}]", query, expanded);
                        return expanded;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("==> [RAG] Query expansion failed, fallback to original. Reason: {}", e.getMessage());
        }
        return query;
    }

    private String buildPrompt(String query) {
        return """
                 You are an expert movie trivia and database search assistant.
                 Your task is to translate a user's descriptive, cultural, or vague search query into highly relevant keywords for an Elasticsearch movie database.

                               Rules:
                               1. Identify the exact movie being described.
                               2. Output the exact Movie Title, Main Characters, and 1-2 core themes.
                               3. Return ONLY the keywords separated by spaces.
                               4. Do NOT write sentences, explanations, or conversational text.
                               5. If the query is already a movie title, just return the title.

                               Examples:
                               Query: revenge for a dead dog
                               Keywords: John Wick Keanu Reeves assassin hitman action

                               Query: green ogre and a talking donkey
                               Keywords: Shrek Donkey Fiona Farquaad animation

                               Query: space survival story on mars
                               Keywords: The Martian Matt Damon astronaut sci-fi

                               Query: %s

                Keywords:"""
                .formatted(query);
    }

    private boolean isValidExpansion(String expanded) {
        if (expanded == null || expanded.isBlank())
            return false;
        if (expanded.length() > 300)
            return false;
        if (expanded.contains("```"))
            return false;
        return true;
    }
}
