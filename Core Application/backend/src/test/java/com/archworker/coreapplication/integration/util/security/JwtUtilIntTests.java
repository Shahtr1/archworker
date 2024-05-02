package com.archworker.coreapplication.integration.util.security;

import com.archworker.coreapplication.util.security.JwtUtil;
import com.archworker.coreapplication.util.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
public class JwtUtilIntTests {

    @Autowired
    private JwtUtil jwtUtil;

    private UserDetails userDetails;
    private String validToken;
    private String expiredToken;

    @BeforeEach
    void setUp() {

        String username = "integrationTestUser";
        userDetails = new User(username, "password", Collections.emptyList());
        validToken = jwtUtil.generateToken(username);

        // Set to past date
        Date expiredDate = new Date(System.currentTimeMillis() - 1000);
        expiredToken = getModifiedDateToken(validToken, expiredDate);
    }

    private String getModifiedDateToken(String token, Date date) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(
                        Keys.hmacShaKeyFor(
                                Decoders.BASE64.decode(SecurityConstants.TOKEN_SECRET)))
                .build().parseClaimsJws(token).getBody();


        claims.setExpiration(date);
        return Jwts.builder().setClaims(claims).signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SecurityConstants.TOKEN_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    @DisplayName("Should Extract Correct Username From Token")
    void shouldExtractCorrectUsernameFromToken() {
        assertEquals(userDetails.getUsername(), jwtUtil.extractUsername(validToken));
    }

    @Test
    @DisplayName("Should Detect Expired Tokens Accurately")
    void shouldDetectExpiredTokensAccurately() {
        assertThrows(ExpiredJwtException.class,
                () -> jwtUtil.validateToken(expiredToken, userDetails));
    }

    @Test
    @DisplayName("Should Validate Token Successfully With Correct Credentials")
    void shouldValidateTokenSuccessfullyWithCorrectCredentials() {
        assertTrue(jwtUtil.validateToken(validToken, userDetails));
    }

    @Test
    @DisplayName("Should Generate Consistent Token For Same User")
    void shouldGenerateConsistentTokenForSameUser() {
        String anotherToken = jwtUtil.generateToken(userDetails.getUsername());
        assertEquals(jwtUtil.extractUsername(validToken), jwtUtil.extractUsername(anotherToken));
        assertFalse(jwtUtil.isTokenExpired(anotherToken));
    }

    @Test
    @DisplayName("Should Reject Token With Tampered Signature")
    void shouldRejectTokenWithTamperedSignature() {
        String invalidToken = validToken.substring(0, validToken.lastIndexOf(".") + 1) + "tamperedSignature";
        assertThrows(Exception.class, () -> jwtUtil.validateToken(invalidToken, userDetails));
    }

    @Test
    @DisplayName("Should Handle Boundary Case Expirations Correctly")
    void shouldHandleBoundaryCaseExpirationsCorrectly() {
        // Assuming you have a mechanism to just set the expiration time near current time
        long nearExpireDuration = 1000 * 5; // 5 seconds in the future
        Date nearExpirationDate = new Date(System.currentTimeMillis() + nearExpireDuration);
        String nearExpireToken = getModifiedDateToken(
                jwtUtil.generateToken(userDetails.getUsername()),
                nearExpirationDate);
        assertFalse(jwtUtil.isTokenExpired(nearExpireToken));
        // Simulate waiting for the token to expire
        try {
            Thread.sleep(5000 + 1000); // sleep time is slightly more than token expiration time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertThrows(ExpiredJwtException.class,
                () -> jwtUtil.validateToken(nearExpireToken, userDetails));
    }
}
