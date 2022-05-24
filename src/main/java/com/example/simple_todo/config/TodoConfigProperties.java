package com.example.simple_todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(prefix = "todo")
public class TodoConfigProperties {

    private Jwt jwt;

    @Data
    @Component
    public static class Jwt {
        private String secret;
        private Long tokenValidityInMillis;
    }
}
