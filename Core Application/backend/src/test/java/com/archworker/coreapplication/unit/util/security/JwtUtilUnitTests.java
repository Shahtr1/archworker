package com.archworker.coreapplication.unit.util.security;

import com.archworker.coreapplication.util.security.JwtUtil;
import com.archworker.coreapplication.util.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;

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
        String username = "user";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(username);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(
                        Keys.hmacShaKeyFor(
                                Decoders.BASE64.decode(SecurityConstants.TOKEN_SECRET)))
                .build().parseClaimsJws(token).getBody();

        // Set to past date
        claims.setExpiration(new Date(System.currentTimeMillis() - 1000));

        String expiredToken = Jwts.builder().setClaims(claims).compact();

        // Act & Assert
        assertThrows(ExpiredJwtException.class,
                ()->jwtUtil.validateToken(expiredToken, userDetails));
    }

    @Test
    @DisplayName("Token with invalid signature should not validate")
    void whenTokenSignatureIsInvalid_thenShouldNotValidate() {
        // Arrange
        String username = "user";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(username);

        // Tamper with the token to make the signature invalid
        String invalidToken = token.substring(0, token.lastIndexOf(".") + 1);

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtUtil.validateToken(invalidToken, userDetails),
                "Invalid signature should cause JwtException");

    }
}
