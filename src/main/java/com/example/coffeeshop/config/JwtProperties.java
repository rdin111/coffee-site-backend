package com.example.coffeeshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app") // Binds all properties starting with "app"
@Data // Lombok for getters/setters
public class JwtProperties {
    private String jwtSecret;
    private String  jwtExpirationInMs;
}
