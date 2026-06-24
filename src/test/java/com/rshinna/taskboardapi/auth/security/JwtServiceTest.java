package com.rshinna.taskboardapi.auth.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "minha-chave-super-secreta-minimo-32-caracteres");

        ReflectionTestUtils.setField(
                jwtService,
                "expirationMs",
                3600000L);
    }

    @Test
    void shouldGenerateToken() {

        String token =
        jwtService.generateToken(
                "rodrigo@email.com");

assertNotNull(token);
assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token =
        jwtService.generateToken(
                "rodrigo@email.com");

String email =
        jwtService.extractEmail(token);

assertEquals(
        "rodrigo@email.com",
        email);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        String token =
        jwtService.generateToken(
                "rodrigo@email.com");

assertTrue(
        jwtService.validateToken(token));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        assertFalse(
        jwtService.validateToken(
                "token-invalido"));
    }
}
