package com.saattech.config;

import com.saattech.config.properties.SpringAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class SpringAiConfig {

    private final SpringAiProperties aiProperties;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) throws Exception {

        String promptTemplate = StreamUtils.copyToString(aiProperties.getPromptPath().getInputStream(), StandardCharsets.UTF_8);

        return chatClientBuilder
                .defaultSystem(promptTemplate)
                .build();
    }
}
