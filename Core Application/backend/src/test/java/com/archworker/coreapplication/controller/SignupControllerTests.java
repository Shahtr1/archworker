package com.archworker.coreapplication.controller;

import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebMvcTest(controllers = SignupController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class SignupControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("User can be created")
    void testSignUp_whenValidUserDetailsProvided_returnsConfirmationString() throws Exception {
//        Arrange
        String successMessage = "User created successfully!";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test@test.com");
        signupDTO.setName("Test");
        signupDTO.setPassword("test");

        when(authService.createUser(Mockito.any())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(new ObjectMapper().writeValueAsString(signupDTO));

//        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

//        Assert
        assertEquals(successMessage,responseBodyAsString);

    }

    @Test
    @DisplayName("User cannot be created with invalid details")
    void testSignUp_whenInvalidUserDetailsProvided_returnsFailureString() throws Exception {
        // Arrange
        String failedMessage = "Failed to create user";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("invalid@invalid.com");
        signupDTO.setName("Invalid");
        signupDTO.setPassword("invalid");

        when(authService.createUser(Mockito.any())).thenReturn(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(new ObjectMapper().writeValueAsString(signupDTO));

        //        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        //        Assert
        assertEquals(failedMessage,responseBodyAsString);
    }

}
