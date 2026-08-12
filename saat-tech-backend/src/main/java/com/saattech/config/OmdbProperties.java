package com.saattech.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OmdbProperties {
    @Value("${omdb.api.url}")
    private String apiUrl;
    @Value("${omdb.api.key}")
    private String apiKey;
}

