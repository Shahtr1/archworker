package com.archworker.coreapplication.unit.util.security;

import com.archworker.coreapplication.util.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilUnitTests {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Generated token should contain correct username and not be expired")
    void whenGenerateToken_thenCorrectFormat() {
        // Arrange
        String username = "user";
        String token = jwtUtil.generateToken(username);

        // Act & assert
        assertEquals(username, jwtUtil.extractUsername(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("Valid token should pass validation")
    void whenTokenIsValid_thenValidateSuccessfully() {

        // Arrange
        String username = "user";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(username);

        // Act
        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid, "Token should be valid");
    }

    @Test
    @DisplayName("Expired token should not be valid")
    void whenTokenIsExpired_thenShouldNotValidate() {
        // Arrange
        // Act
        // Assert
    }

    @Test
    @DisplayName("Token with invalid signature should not validate")
    void whenTokenSignatureIsInvalid_thenShouldNotValidate() {
        // Arrange
        // Act
        // Assert
    }
}
