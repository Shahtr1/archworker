package com.archworker.coreapplication.integration.filter;

import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.entity.Role;
import com.archworker.coreapplication.enums.RoleEnum;
import com.archworker.coreapplication.filter.JwtRequestFilter;
import com.archworker.coreapplication.repository.UserRepository;
import com.archworker.coreapplication.service.AuthService;
import com.archworker.coreapplication.service.jwt.UserServiceImpl;
import com.archworker.coreapplication.util.security.JwtUtil;
import com.archworker.coreapplication.util.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class JwtRequestFilterIntTests {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    List<SimpleGrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userRepository.deleteAll();

        Role userRole = new Role();
        userRole.setRole(RoleEnum.USER);

        authorities = Stream.of(userRole)
                .map(role -> new SimpleGrantedAuthority(role.getRole().getRoleName()))
                .toList();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test doFilterInternal with no authorization header")
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
            // No-op implementation for FilterChain
        };

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

    @Test
    @DisplayName("Test doFilterInternal with invalid authorization header")
    void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(SecurityConstants.HEADER_STRING, "InvalidToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
            // No-op implementation for FilterChain
        };

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

    @Test
    @DisplayName("Test doFilterInternal with valid authorization header but no username extracted")
    void testDoFilterInternal_ValidAuthorizationHeader_NoUsernameExtracted() throws ServletException, IOException {
        // Arrange
        String username = "integrationTestUser1";
        UserDetails userDetails = new User(username, "password", authorities);
        String validToken = jwtUtil.generateToken(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        String authHeader = SecurityConstants.TOKEN_PREFIX + validToken;
        request.addHeader(SecurityConstants.HEADER_STRING, authHeader);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
            // No-op implementation for FilterChain
        };

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                ()->jwtRequestFilter
                        .doFilterInternal(request, response, filterChain));

    }

    @Test
    @DisplayName("Test doFilterInternal with valid authorization header, username extracted, no authentication in SecurityContext")
    void testDoFilterInternal_ValidAuthorizationHeader_UsernameExtracted_NoAuthentication() throws ServletException, IOException, RoleNotFoundException {
        // Arrange
        String username = "jwtRequestTestUser";
        String email = "jwtRequestTestUser@test.com";
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail(email);
        signupDTO.setPassword("securePassword");
        signupDTO.setName(username);

        authService.createUser(signupDTO);


        UserDetails userDetails = new User(email, "password", authorities);
        String validToken = jwtUtil.generateToken(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        String authHeader = SecurityConstants.TOKEN_PREFIX + validToken;
        request.addHeader(SecurityConstants.HEADER_STRING, authHeader);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
            // No-op implementation for FilterChain
        };


        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());

    }



    @Test
    @DisplayName("Test doFilterInternal with valid authorization header and existing authentication in SecurityContext")
    void testDoFilterInternal_ValidAuthorizationHeader_AuthenticationAlreadyPresent() throws ServletException, IOException, RoleNotFoundException {
        // Arrange
        String username = "jwtRequestTestUser1";
        String email = "jwtRequestTestUser1@test.com";
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail(email);
        signupDTO.setPassword("securePassword");
        signupDTO.setName(username);

        // Create user
        authService.createUser(signupDTO);

        UserDetails userDetailsCreation = new User(email, "password", authorities);
        String validToken = jwtUtil.generateToken(userDetailsCreation);

        MockHttpServletRequest request = new MockHttpServletRequest();
        String authHeader = SecurityConstants.TOKEN_PREFIX + validToken;
        request.addHeader(SecurityConstants.HEADER_STRING, authHeader);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
            // No-op implementation for FilterChain
        };

        // Set existing authentication in SecurityContext
        UserDetails userDetails = userService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());

    }


}