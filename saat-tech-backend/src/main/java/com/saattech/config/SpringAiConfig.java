package com.saattech.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Configuration
public class SpringAiConfig {

    @Value("classpath:prompts/rag-prompt.txt")
    private Resource promptResource;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) throws Exception {

        String promptTemplate = StreamUtils.copyToString(promptResource.getInputStream(), StandardCharsets.UTF_8);

        return chatClientBuilder
                .defaultSystem(promptTemplate)
                .build();
    }
}
