package com.archworker.coreapplication.integration.service;

import com.archworker.coreapplication.entity.User;
import com.archworker.coreapplication.repository.UserRepository;
import com.archworker.coreapplication.service.jwt.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplIntTests {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Loads user by username with correct details")
    void testUserService_whenValidEmailProvided_returnsUserDetails() {
        // Arrange
        User user1 = new User();
        user1.setEmail("user1@email.com");
        user1.setName("user1Name");
        user1.setPassword(passwordEncoder.encode("user1Password"));
        userRepository.save(user1);

        // Act
        UserDetails userDetails = userServiceImpl.loadUserByUsername(user1.getEmail());

        // Assert
        assertEquals(user1.getEmail(), userDetails.getUsername());
        assertTrue(passwordEncoder.matches("user1Password", userDetails.getPassword()), "Passwords do not match");
        assertEquals(0, userDetails.getAuthorities().size(), "No authorities should be granted");
    }

    @Test
    @DisplayName("Throws UsernameNotFoundException when user not found")
    void testUserService_whenEmailNotFound_throwsException() {
        // Arrange
        String email = "nonexistent@email.com";

        // Act and Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userServiceImpl.loadUserByUsername(email),
                "Expected UsernameNotFoundException to be thrown when the user is not found");

    }

}
