package com.citamedica.backend.adapter.out.integration.calcom;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CalcomClient {

    private static final Logger logger = LoggerFactory.getLogger(CalcomClient.class);

    private final RestTemplate restTemplate;
    private final String calcomApiUrl;
    private final String calcomApiKey;

    public CalcomClient(RestTemplate restTemplate,
                        @Value("${calcom.api.url}") String calcomApiUrl,
                        @Value("${calcom.api.key:#{null}}") String calcomApiKey) {
        this.restTemplate = restTemplate;
        this.calcomApiUrl = calcomApiUrl;
        this.calcomApiKey = calcomApiKey;
    }

    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CalcomTeam createTeam(String name, String slug) {
        String url = calcomApiUrl + "/teams";
        CalcomTeamRequest request = new CalcomTeamRequest(name, slug);
        logger.info("Creating team: {} with slug: {}", name, slug);
        try {
            CalcomTeam team = restTemplate.postForObject(url, request, CalcomTeam.class);
            logger.info("Team created successfully: {}", team.id);
            return team;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error creating team: {}", e.getMessage());
            throw new CalcomApiException("Error creating team: " + e.getMessage());
        }
    }

    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CalcomUser createUser(String email, String username, String name) {
        String url = calcomApiUrl + "/users";
        CalcomUserRequest request = new CalcomUserRequest(email, username, name);
        logger.info("Creating user: {} with username: {}", name, username);
        try {
            CalcomUser user = restTemplate.postForObject(url, request, CalcomUser.class);
            logger.info("User created successfully: {}", user.id);
            return user;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw new CalcomApiException("Error creating user: " + e.getMessage());
        }
    }

    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void addTeamMember(String teamId, String userId) {
        String url = calcomApiUrl + "/teams/" + teamId + "/members";
        CalcomMemberRequest request = new CalcomMemberRequest(userId);
        logger.info("Adding member {} to team {}", userId, teamId);
        try {
            restTemplate.postForObject(url, request, Void.class);
            logger.info("Member added successfully");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error adding team member: {}", e.getMessage());
            throw new CalcomApiException("Error adding team member: " + e.getMessage());
        }
    }

    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CalcomBooking getBooking(String bookingId) {
        String url = calcomApiUrl + "/bookings/" + bookingId;
        logger.info("Getting booking: {}", bookingId);
        try {
            CalcomBooking booking = restTemplate.getForObject(url, CalcomBooking.class);
            logger.info("Booking retrieved successfully: {}", booking.id);
            return booking;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error getting booking: {}", e.getMessage());
            throw new CalcomApiException("Error getting booking: " + e.getMessage());
        }
    }

    // DTOs
    public static class CalcomTeamRequest {
        public String name;
        public String slug;

        public CalcomTeamRequest(String name, String slug) {
            this.name = name;
            this.slug = slug;
        }
    }

    public static class CalcomUserRequest {
        public String email;
        public String username;
        public String name;

        public CalcomUserRequest(String email, String username, String name) {
            this.email = email;
            this.username = username;
            this.name = name;
        }
    }

    public static class CalcomMemberRequest {
        public String userId;

        public CalcomMemberRequest(String userId) {
            this.userId = userId;
        }
    }

    // Response DTOs
    public static class CalcomTeam {
        public String id;
        public String name;
        public String slug;
    }

    public static class CalcomUser {
        public String id;
        public String email;
        public String username;
        public String name;
    }

    public static class CalcomBooking {
        public String id;
        public String uid;
        public String title;
        public String startTime;
        public String endTime;
        // Add other fields as needed
    }
}