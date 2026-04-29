package com.citamedica.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "test-jwt-secret-very-long-and-safe");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 3_600_000L);
    }

    @Test
    void generateAndParseToken_withValidAuthentication_returnsExpectedClaims() {
        UserPrincipal principal = UserPrincipal.create(42L, "admin", "admin@test.com", List.of("ADMIN", "STAFF"));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = tokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("admin", tokenProvider.getUsernameFromJWT(token));
        assertEquals(42L, tokenProvider.getUserIdFromJWT(token));
        assertEquals(List.of("ROLE_ADMIN", "ROLE_STAFF"), tokenProvider.getRolesFromJWT(token));
    }

    @Test
    void validateToken_withMalformedToken_returnsFalse() {
        assertFalse(tokenProvider.validateToken("not-a-jwt-token"));
    }

    @Test
    void validateToken_withExpiredToken_returnsFalse() throws InterruptedException {
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 1L);

        UserPrincipal principal = new UserPrincipal(1L, "short", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = tokenProvider.generateToken(authentication);
        Thread.sleep(5L);

        assertFalse(tokenProvider.validateToken(token));
    }
}
