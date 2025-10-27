package com.citamedica.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RestTemplateConfig {

    @Value("${calcom.api.key}")
    private String calcomApiKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // If needed, configure RestTemplate with interceptors for authentication
}