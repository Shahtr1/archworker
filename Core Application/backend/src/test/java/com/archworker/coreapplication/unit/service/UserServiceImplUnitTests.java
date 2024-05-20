package com.archworker.coreapplication.unit.service;

import com.archworker.coreapplication.entity.Role;
import com.archworker.coreapplication.entity.User;
import com.archworker.coreapplication.enums.RoleEnum;
import com.archworker.coreapplication.repository.UserRepository;
import com.archworker.coreapplication.service.jwt.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    @DisplayName("Loads user by username")
    void testUserService_whenValidEmailProvided_returnsUserDetails() {
        // Arrange
        User dummyUser = new User();
        dummyUser.setEmail("dummy@email.com");
        dummyUser.setName("dummyName");
        dummyUser.setPassword("dummyPassword");

        Role role = new Role();
        role.setRole(RoleEnum.USER);

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        dummyUser.setRoles(roles);

        when(userRepository.findByEmail("dummy@email.com")).thenReturn(Optional.of(dummyUser));

        // Act
        UserDetails userDetails = userServiceImpl.loadUserByUsername(dummyUser.getEmail());

        // Assert
        assertEquals(userDetails.getUsername(), dummyUser.getEmail());
        assertEquals(userDetails.getPassword(), dummyUser.getPassword());
        assertEquals(1, userDetails.getAuthorities().size(), "One authority should be granted");
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());

    }

    @Test
    @DisplayName("Throws UsernameNotFoundException when user not found")
    void testUserService_whenEmailNotFound_throwsException() {
        // Arrange
        User dummyUser = new User();
        dummyUser.setEmail("nonexistent@email.com");
        dummyUser.setName("dummyName");
        dummyUser.setPassword("dummyPassword");

        when(userRepository.findByEmail(dummyUser.getEmail())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userServiceImpl.loadUserByUsername("nonexistent@email.com"),
                "Expected UsernameNotFoundException to be thrown when the user is not found");

    }

}
