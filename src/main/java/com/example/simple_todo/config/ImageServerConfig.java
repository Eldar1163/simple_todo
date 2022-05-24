package com.example.simple_todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "image-server")
public class ImageServerConfig {
    private String path;
    private String username;
    private String password;
}
