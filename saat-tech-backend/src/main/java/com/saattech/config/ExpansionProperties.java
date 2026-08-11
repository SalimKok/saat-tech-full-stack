package com.saattech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.expansion")
public class ExpansionProperties {
    private String model;
    private boolean enabled;
    private int minWords;
}
