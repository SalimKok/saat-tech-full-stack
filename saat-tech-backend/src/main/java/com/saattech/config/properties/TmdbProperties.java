package com.saattech.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "tmdb.api")
public class TmdbProperties {
    private String baseUrl;
    private String apiKey;
}
