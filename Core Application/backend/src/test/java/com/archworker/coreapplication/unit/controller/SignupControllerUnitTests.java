package com.archworker.coreapplication.unit.controller;

import com.archworker.coreapplication.controller.SignupController;
import com.archworker.coreapplication.dto.ErrorDTO;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebMvcTest(controllers = SignupController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class SignupControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("User can be created")
    void testSignUp_whenValidUserDetailsProvided_returnsConfirmationString() throws Exception {
//        Arrange
        String successMessage = "User created successfully!";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test@test.com");
        signupDTO.setName("Test");
        signupDTO.setPassword("shahrukh");

        when(authService.createUser(Mockito.any())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(new ObjectMapper().writeValueAsString(signupDTO));

//        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

//        Assert
        assertEquals(successMessage, responseBodyAsString);

    }

    @Test
    @DisplayName("User cannot be created when service has error")
    void testSignUp_whenInvalidUserDetailsProvided_returnsFailureString() throws Exception {
        // Arrange
        String failedMessage = "Failed to create user";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test@test.com");
        signupDTO.setName("Test");
        signupDTO.setPassword("shahrukh");

        when(authService.createUser(Mockito.any())).thenReturn(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(objectMapper.writeValueAsString(signupDTO));

        //        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ErrorDTO errorDTO = objectMapper.readValue(responseBodyAsString, ErrorDTO.class);

        //        Assert
        assertEquals(failedMessage, errorDTO.getError());
    }

    @Test
    @DisplayName("User cannot be created with invalid email format")
    void testSignUp_whenEmailIsInvalid_returnsInvalidEmail() throws Exception {
        // Arrange
        String failedMessage = "Email must be a valid email address";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("invalid.com");
        signupDTO.setName("validName");
        signupDTO.setPassword("validPassword");

        when(authService.createUser(Mockito.any())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(objectMapper.writeValueAsString(signupDTO));

        //        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ErrorDTO errorDTO = objectMapper.readValue(responseBodyAsString, ErrorDTO.class);
        List<String> messages = errorDTO.getMessages();


        //        Assert
        assertTrue(messages.contains("email: " + failedMessage));
    }

    @Test
    @DisplayName("User cannot be created with name having less than 2 characters")
    void testSignUp_whenNameIsLessThan2Characters_returnsInvalidName() throws Exception {
        // Arrange
        String failedMessage = "Name must be at least 2 characters long";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("valid@valid.com");
        signupDTO.setName("v");
        signupDTO.setPassword("validPassword");

        when(authService.createUser(Mockito.any())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(objectMapper.writeValueAsString(signupDTO));

        //        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ErrorDTO errorDTO = objectMapper.readValue(responseBodyAsString, ErrorDTO.class);
        List<String> messages = errorDTO.getMessages();


        //        Assert
        assertTrue(messages.contains("name: " + failedMessage));
    }

    @Test
    @DisplayName("User cannot be created with password having less than 8 characters")
    void testSignUp_whenPasswordIsLessThan8Characters_returnsInvalidPassword() throws Exception {
        // Arrange
        String failedMessage = "Password must be at least 8 characters long";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("valid@valid.com");
        signupDTO.setName("validName");
        signupDTO.setPassword("wrong");

        when(authService.createUser(Mockito.any())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(objectMapper.writeValueAsString(signupDTO));

        //        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ErrorDTO errorDTO = objectMapper.readValue(responseBodyAsString, ErrorDTO.class);
        List<String> messages = errorDTO.getMessages();


        //        Assert
        assertTrue(messages.contains("password: " + failedMessage));
    }

    @Test
    @DisplayName("User cannot be created if email field is missing")
    void testSignUp_whenEmailFieldMissing_returnsRequiredError() throws Exception {
        // Arrange
        String failedMessage = "Email is required";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setName("validName");
        signupDTO.setPassword("validPassword");

        when(authService.createUser(Mockito.any())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(objectMapper.writeValueAsString(signupDTO));

        //        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ErrorDTO errorDTO = objectMapper.readValue(responseBodyAsString, ErrorDTO.class);
        List<String> messages = errorDTO.getMessages();


        //        Assert
        assertTrue(messages.contains("email: " + failedMessage));
    }

    @Test
    @DisplayName("User cannot be created if name field is missing")
    void testSignUp_whenNameFieldMissing_returnsRequiredError() throws Exception {
        // Arrange
        String failedMessage = "Name is required";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("valid@valid.com");
        signupDTO.setPassword("validPassword");

        when(authService.createUser(Mockito.any())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(objectMapper.writeValueAsString(signupDTO));

        //        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ErrorDTO errorDTO = objectMapper.readValue(responseBodyAsString, ErrorDTO.class);
        List<String> messages = errorDTO.getMessages();


        //        Assert
        assertTrue(messages.contains("name: " + failedMessage));
    }

    @Test
    @DisplayName("User cannot be created if password field is missing")
    void testSignUp_whenPasswordFieldMissing_returnsRequiredError() throws Exception {
        // Arrange
        String failedMessage = "Password is required";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setName("validName");
        signupDTO.setEmail("valid@valid.com");

        when(authService.createUser(Mockito.any())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(objectMapper.writeValueAsString(signupDTO));

        //        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        ErrorDTO errorDTO = objectMapper.readValue(responseBodyAsString, ErrorDTO.class);
        List<String> messages = errorDTO.getMessages();


        //        Assert
        assertTrue(messages.contains("password: " + failedMessage));
    }


}
