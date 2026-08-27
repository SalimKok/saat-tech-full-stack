package com.saattech.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@Data
@ConfigurationProperties(prefix = "app.ai")
public class SpringAiProperties {
    private Resource promptPath;
}

