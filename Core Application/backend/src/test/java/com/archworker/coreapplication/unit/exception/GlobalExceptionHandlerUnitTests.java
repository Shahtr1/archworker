package com.archworker.coreapplication.unit.exception;

import com.archworker.coreapplication.dto.ErrorDTO;
import com.archworker.coreapplication.exception.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerUnitTests {
    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("web request description");
    }

    @Test
    void testHandleConstraintViolationException() {
        // Mock ConstraintViolationException and the ConstraintViolations
        ConstraintViolation<?> mockViolation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<?>> violations = Set.of(mockViolation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        // Configure the mock to return specific data

        // This line creates a mock of the javax.validation.Path class, which represents the property path in a constraint violation
        // (i.e., where in your data model the violation occurred).
        Path mockPath = mock(Path.class);

        when(mockPath.toString()).thenReturn("field");
        when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        when(mockViolation.getMessage()).thenReturn("must not be null");

        // Call the method
        ResponseEntity<ErrorDTO> response = handler.handleConstraintViolationException(exception, webRequest);

        // Assertions on the response status
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status code should be BAD_REQUEST");

        // Assertions on the response body
        assertNotNull(response.getBody(), "ErrorDTO should not be null");

        // Detailed assertions on the ErrorDTO contents
        ErrorDTO errorDTO = response.getBody();
        assertNotNull(errorDTO.getTimestamp(), "Timestamp should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorDTO.getStatus(), "Error status should match BAD_REQUEST");
        assertEquals("Validation Error", errorDTO.getError(), "Error description should match 'Validation Error'");
        assertEquals("web request description", errorDTO.getPath(), "Error path should match the mock WebRequest description");

        // Ensure that the errors list contains the expected message
        assertNotNull(errorDTO.getMessages(), "Errors list should not be null");
        assertTrue(errorDTO.getMessages().contains("field: must not be null"), "Errors list should contain the correct violation message");

        // Check that the errors list size is correct
        assertEquals(1, errorDTO.getMessages().size(), "Errors list should contain exactly one entry");
    }


    @Test
    void testHandleMethodArgumentNotValidException() {
        // Mock MethodArgumentNotValidException and BindingResult/FieldErrors
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "field", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Call the method
        ResponseEntity<ErrorDTO> response = handler.handleMethodArgumentNotValidException(exception, webRequest);

        // Assertions on the response status and body
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        // Detailed assertions on the contents of ErrorDTO
        ErrorDTO errorDTO = response.getBody();
        assertNotNull(errorDTO.getTimestamp(), "Timestamp should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorDTO.getStatus(), "Error status should match BAD_REQUEST");
        assertEquals("Validation Error", errorDTO.getError(), "Error description should match 'Validation Error'");
        assertEquals("web request description", errorDTO.getPath(), "Error path should match the mock WebRequest description");

        // Assert the details of the errors returned
        assertNotNull(errorDTO.getMessages(), "Errors list should not be null");
        assertFalse(errorDTO.getMessages().isEmpty(), "Errors list should not be empty");
        assertTrue(errorDTO.getMessages().contains("field: must not be null"), "Errors list should contain the correct violation message");
        assertEquals(1, errorDTO.getMessages().size(), "Errors list should contain exactly one entry");
    }

}
