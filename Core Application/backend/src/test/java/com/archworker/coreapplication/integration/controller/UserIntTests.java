package com.archworker.coreapplication.integration.controller;

import com.archworker.coreapplication.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntTests {
    @Value("${server.port}")
    private int serverPort;

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Autowired
    private ObjectMapper objectMapper;

    private String authorizationToken;

    @Test
    void contextLoads() {
        System.out.println("server.port=" + serverPort);
        System.out.println("localServerPort=" + localServerPort);
    }

    @Test
    @DisplayName("User can be created")
    @Order(1)
    void testCreateUser_whenValidDetailsProvided_returnsConfirmationString() {
        //        Arrange
        String successMessage = "User created successfully!";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test@test.com");
        signupDTO.setName("Test");
        signupDTO.setPassword("shahrukh");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupDTO> request = new HttpEntity<>(signupDTO, headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.postForEntity("/signup", request, String.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(successMessage, response.getBody());
    }

    @Test
    @DisplayName("GET /hello requires JWT")
    @Order(2)
    void testGetHello_whenMissingJWT_returns403() {
//        Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);

//        Act
        ResponseEntity<List<HelloDTO>> response =
                testRestTemplate.exchange("/api/hello", HttpMethod.GET, httpEntity,
                        new ParameterizedTypeReference<>() {
                        });

//        Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
                "HTTP Status 403 should have been returned");
    }

    @Test
    @DisplayName("/login works")
    @Order(3)
    void testUserLogin_whenValidCredentialsProvided_returnsJWTinAuthorizationHeader() {
//        Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@test.com");
        loginDTO.setPassword("shahrukh");

        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO);

//        Act
        ResponseEntity<JwtDTO> response = testRestTemplate.postForEntity("/login",
                request, JwtDTO.class);

        JwtDTO jwtDTO = response.getBody();
        assert jwtDTO != null;
        authorizationToken = jwtDTO.jwt();

//        Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status code should be 200");
        assertNotNull(authorizationToken,
                "Response should contain Authorization header with JWT");
    }

    @Test
    @Order(5)
    @DisplayName("GET /hello works")
    void testGetHello_whenValidJWTProvided_returnsDTO() {
//        Arrange
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(authorizationToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);

//        Act
        ResponseEntity<HelloDTO> response = testRestTemplate.exchange("/api/hello",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        HelloDTO helloDTO = response.getBody();

//        Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP status code should be 200");

        assert helloDTO != null;
        assertEquals(helloDTO.message(),
                "Hello from Authorized API request.");

    }

    @Test
    @DisplayName("User cannot be created if duplicate email")
    @Order(6)
    void testCreateUser_whenDuplicateEmail_returnsError() throws JsonProcessingException {
        //        Arrange
        String failedMessage = "User email already exists.";

        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail("test@test.com");
        signupDTO.setName("Test");
        signupDTO.setPassword("shahrukh");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupDTO> request = new HttpEntity<>(signupDTO, headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.postForEntity("/signup", request, String.class);
        ErrorDTO errorDTO = objectMapper.readValue(response.getBody(), ErrorDTO.class);
        List<String> messages = errorDTO.getMessages();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(messages.contains("email: " + failedMessage));

    }


}
