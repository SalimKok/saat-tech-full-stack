package com.saattech.elasticsearch.service.impl;

import com.saattech.config.properties.SearchProperties;
import com.saattech.elasticsearch.service.QueryExpansionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaQueryExpansionServiceImpl implements QueryExpansionService {

    private final ChatClient chatClient;
    private final SearchProperties searchProperties;

    @Override
    public boolean needsExpansion(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        String[] words = query.trim().split("\\s+");
        return words.length >= searchProperties.getExpansion().getMinWord();
    }

    @Override
    public String expand(String query) {
        try {

            String expanded = chatClient.prompt(query).call().content();

            if (expanded != null) {
                if (expanded.contains("Keywords:")) {
                    expanded = expanded.substring(expanded.lastIndexOf("Keywords:") + 9);
                }

                expanded = expanded.replaceAll("(?i)it sounds like.*|here are.*", "").trim();
                if (isValidExpansion(expanded)) {
                    log.info("==> [RAG] Query expanded (Spring AI): [{}] → [{}]", query, expanded);
                    return expanded;
                }
            }
        }
        catch (Exception e) {
            log.warn("==> [RAG] Spring AI Query expansion failed, fallback to original. Reason: {}", e.getMessage());
        }
        return query;
    }

    private boolean isValidExpansion(String expanded) {
        return expanded.length() < 300 && !expanded.contains("{") && !expanded.contains("}");
    }
}