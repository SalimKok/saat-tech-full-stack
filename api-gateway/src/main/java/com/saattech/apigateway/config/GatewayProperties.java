package com.saattech.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway")
@Getter
@Setter
public class GatewayProperties {
    private String secret;
}
