package com.archworker.coreapplication.integration.controller;

import com.archworker.coreapplication.dto.SignupDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignupControllerIntTests {
    @Value("${server.port}")
    private int serverPort;

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;

    @Test
    void contextLoads(){
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

        String url = "http://localhost:" + localServerPort + "/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupDTO> request = new HttpEntity<>(signupDTO, headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.postForEntity(url, request, String.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(successMessage, response.getBody());
    }
}
