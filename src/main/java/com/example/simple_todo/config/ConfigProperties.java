package com.example.simple_todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "todo")
public class ConfigProperties {

    private Jwt jwt;

    private String imageServerPath;

    @Data
    public static class Jwt {
        private String secret;
        private Long tokenValidityInMillis;
    }
}
