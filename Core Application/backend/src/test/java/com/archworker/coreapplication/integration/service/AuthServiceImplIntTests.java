package com.archworker.coreapplication.integration.service;

import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.entity.User;
import com.archworker.coreapplication.repository.UserRepository;
import com.archworker.coreapplication.service.AuthServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceImplIntTests {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("User creation successful and returns TRUE")
    @Order(1)
    void testCreateUser_whenValidUserDetailsProvided_returnsTrue() {
        // Arrange
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test2@test.com");
        signupDTO.setPassword("securePassword");
        signupDTO.setName("test");

        // Act
        boolean result = authService.createUser(signupDTO);

        // Assert
        assertTrue(result, "The result should be true when the user is successfully created");
        assertTrue(userRepository.existsByEmail(signupDTO.getEmail()));
        User user = userRepository.findByEmail(signupDTO.getEmail()).orElseThrow();
        assertTrue(passwordEncoder.matches(signupDTO.getPassword(), user.getPassword()), "The password should be the same");
    }

    @Test
    @DisplayName("User creation fails when email already exists")
    @Order(2)
    public void testCreateUser_ExistingEmail_ReturnsFalse() {
        // Arrange
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test2@test.com");
        signupDTO.setPassword("securePassword");
        signupDTO.setName("shahrukh");

        // Act
        boolean result = authService.createUser(signupDTO);

        // Assert
        assertFalse(result, "The result should be false when email already exists");
        assertTrue(userRepository.existsByEmail(signupDTO.getEmail()));
    }
}
