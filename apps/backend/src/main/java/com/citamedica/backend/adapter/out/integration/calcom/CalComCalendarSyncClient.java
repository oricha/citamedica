package com.citamedica.backend.adapter.out.integration.calcom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Best-effort fetch of busy intervals from Cal.com API for conflict blocking.
 * Returns an empty list when API is not configured or the request fails.
 */
@Service
public class CalComCalendarSyncClient {

    private static final Logger log = LoggerFactory.getLogger(CalComCalendarSyncClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public CalComCalendarSyncClient(
            RestTemplateBuilder builder,
            @Value("${calcom.api.url}") String baseUrl,
            @Value("${calcom.api.key:}") String apiKey) {
        this.restTemplate = builder.build();
        this.baseUrl = baseUrl != null ? baseUrl.replaceAll("/+$", "") : "";
        this.apiKey = apiKey != null ? apiKey : "";
    }

    public List<CalBusyInterval> fetchBusyForUsername(String username, LocalDateTime from, LocalDateTime to) {
        if (username == null || username.isBlank() || apiKey.isBlank()) {
            return List.of();
        }
        try {
            String uri = UriComponentsBuilder.fromUriString(baseUrl + "/bookings")
                    .queryParam("username", username)
                    .queryParam("status", "accepted")
                    .queryParam("take", "200")
                    .build(true)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
            Map<?, ?> body = response.getBody();
            if (body == null || !body.containsKey("data")) {
                return List.of();
            }
            Object data = body.get("data");
            if (!(data instanceof List<?> rawList)) {
                return List.of();
            }
            List<CalBusyInterval> intervals = new ArrayList<>();
            for (Object item : rawList) {
                if (!(item instanceof Map<?, ?> m)) {
                    continue;
                }
                LocalDateTime s = parse((String) m.get("startTime"));
                LocalDateTime e = parse((String) m.get("endTime"));
                if (s != null && e != null && e.isAfter(s)) {
                    intervals.add(new CalBusyInterval(s, e));
                }
            }
            return intervals.stream()
                    .filter(i -> i.end().isAfter(from) && i.start().isBefore(to))
                    .toList();
        } catch (RestClientException ex) {
            log.warn("Cal.com busy fetch failed for {}: {}", username, ex.getMessage());
            return List.of();
        }
    }

    private static LocalDateTime parse(String iso) {
        if (iso == null || iso.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            try {
                return java.time.Instant.parse(iso).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            } catch (Exception e2) {
                return null;
            }
        }
    }
}
