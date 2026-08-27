package com.saattech.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "omdb.api")
public class OmdbProperties {
    private String url;
    private String key;

}
