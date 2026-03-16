package com.hospital.management.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Base64 encoded 256-bit string from application.yml
        String secret = "c2VjcmV0LWtleS1mb3ItaG9zcGl0YWwtbWFuYWdlbWVudC1zeXN0ZW0tMjU2LWJpdC1sZW5ndGgtc3RyaW5nCg==";
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000 * 60 * 60 * 24L); // 24 hours
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("test@hospital.com");

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("test@hospital.com", extractedUsername);
    }

    @Test
    void testIsTokenValid() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("test@hospital.com");

        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }
}
