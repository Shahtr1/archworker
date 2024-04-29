package com.archworker.coreapplication.unit.service;

import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.entity.User;
import com.archworker.coreapplication.repository.UserRepository;
import com.archworker.coreapplication.service.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplUnitTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("User creation successful and returns TRUE")
    void testCreateUser_whenValidUserDetailsProvided_returnsTrue() {
        // Arrange
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test@test.com");
        signupDTO.setPassword("securePassword");
        signupDTO.setName("test");

        User dummyUser = new User();
        dummyUser.setEmail(signupDTO.getEmail());
        dummyUser.setPassword("hashedPassword");

        when(userRepository.existsByEmail(signupDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupDTO.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);

        // Act
        boolean result = authService.createUser(signupDTO);

        // Assert
        assertTrue(result,"The result should be true when the user is successfully created");
        verify(userRepository, times(1)).existsByEmail("test@test.com");
        verify(passwordEncoder, times(1)).encode("securePassword");
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    @DisplayName("User creation fails when email already exists")
    void testCreateUser_whenEmailExists_returnsFalse() {
        // Arrange
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test@test.com");
        signupDTO.setPassword("securePassword");
        signupDTO.setName("test");

        when(userRepository.existsByEmail(signupDTO.getEmail())).thenReturn(true);

        // Act
        boolean result = authService.createUser(signupDTO);

        // Assert
        assertFalse(result, "The result should be false when the email already exists");
        verify(userRepository, times(1)).existsByEmail("test@test.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
