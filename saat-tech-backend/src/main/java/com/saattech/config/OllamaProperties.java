package com.saattech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.ollama")
public class OllamaProperties {
    private String url;

    private Timeout timeout = new Timeout();

    @Data
    public static class Timeout {
        private int connect;
        private int read;
    }
}
