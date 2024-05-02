package com.archworker.coreapplication.unit.configuration;

import com.archworker.coreapplication.configuration.WebSecurityConfiguration;
import com.archworker.coreapplication.filter.JwtRequestFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebSecurityConfigurationUnitTests {

    @Mock
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private HttpSecurity httpSecurity;

    @InjectMocks
    private WebSecurityConfiguration webSecurityConfiguration;

    @Test
    public void testSecurityFilterChain() throws Exception {
        // Arrange
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);


        DefaultSecurityFilterChain defaultSecurityFilterChain = mock(DefaultSecurityFilterChain.class);
        when(httpSecurity.build()).thenReturn(defaultSecurityFilterChain);

        // Act
        SecurityFilterChain result = webSecurityConfiguration.securityFilterChain(httpSecurity);

        // Assert
        assertNotNull(result);
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
