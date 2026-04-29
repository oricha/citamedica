package com.citamedica.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RestTemplateConfig {

    @Value("${calcom.api.key:#{null}}")
    private String calcomApiKey;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // If needed, configure RestTemplate with interceptors for authentication
        // when calcomApiKey is available
        if (calcomApiKey != null && !calcomApiKey.isEmpty()) {
            // Configure authentication interceptor here if needed
        }
        
        return restTemplate;
    }
}