package com.archworker.angulargenerator.integration.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GlobalExceptionHandlerIntTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Bad request when invalid data sent")
    public void whenPostInvalidData_thenBadRequest() throws Exception {
        // TODO: Test it with real scenarios
//        String invalidDataJson = "{\"name\":\"\",\"email\":-1,\"password\":\"\"}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
//                        .content(invalidDataJson)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.error").value("Validation Error"))
//                .andExpect(jsonPath("$.messages").isArray())
//                .andExpect(jsonPath("$.messages", containsInAnyOrder(
//                        "name: Name is required",
//                        "password: Password is required",
//                        "email: Email must be a valid email address",
//                        "name: Name must be at least 2 characters long",
//                        "password: Password must be at least 8 characters long"
//                )));


    }
}
