package com.archworker.coreapplication.unit.filter;

import com.archworker.coreapplication.filter.JwtRequestFilter;
import com.archworker.coreapplication.service.jwt.UserServiceImpl;
import com.archworker.coreapplication.util.security.JwtUtil;
import com.archworker.coreapplication.util.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtRequestFilterUnitTests {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Test doFilterInternal with no authorization header")
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Test doFilterInternal with invalid authorization header")
    void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn("InvalidToken");

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Test doFilterInternal with valid authorization header but no username extracted")
    void testDoFilterInternal_ValidAuthorizationHeader_NoUsernameExtracted() throws ServletException, IOException {
        // Arrange
        String authHeader = SecurityConstants.TOKEN_PREFIX + "ValidToken";
        when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(authHeader);
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Test doFilterInternal with valid authorization header, username extracted, no authentication in SecurityContext")
    void testDoFilterInternal_ValidAuthorizationHeader_UsernameExtracted_NoAuthentication() throws ServletException, IOException {
        // Arrange
        String authHeader = SecurityConstants.TOKEN_PREFIX + "ValidToken";
        String username = "testUser";
        when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(authHeader);
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(username);
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(any(String.class), any(UserDetails.class))).thenReturn(true);
        when(userDetails.getUsername()).thenReturn(username);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }

    @Test
    @DisplayName("Test doFilterInternal with valid authorization header and existing authentication in SecurityContext")
    void testDoFilterInternal_ValidAuthorizationHeader_AuthenticationAlreadyPresent() throws ServletException, IOException {
        // Arrange
        String authHeader = SecurityConstants.TOKEN_PREFIX + "ValidToken";
        String username = "testUser";
        when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(authHeader);
        when(jwtUtil.extractUsername(any(String.class))).thenReturn(username);

        // Simulate an existing authentication in the SecurityContext
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
    }
}
