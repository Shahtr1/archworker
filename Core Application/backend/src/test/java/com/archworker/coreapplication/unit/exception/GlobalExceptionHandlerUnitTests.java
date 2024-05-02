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
        ConstraintViolation<?> mockViolation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<?>> violations = Set.of(mockViolation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);


        // This line creates a mock of the jakarta.validation.Path class, which represents the property path in a constraint violation
        // (i.e., where in your data model the violation occurred).
        Path mockPath = mock(Path.class);

        when(mockPath.toString()).thenReturn("field");
        when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        when(mockViolation.getMessage()).thenReturn("must not be null");

        ResponseEntity<ErrorDTO> response = handler.handleConstraintViolationException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status code should be BAD_REQUEST");

        assertNotNull(response.getBody(), "ErrorDTO should not be null");

        ErrorDTO errorDTO = response.getBody();
        assertNotNull(errorDTO.getTimestamp(), "Timestamp should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorDTO.getStatus(), "Error status should match BAD_REQUEST");
        assertEquals("Validation Error", errorDTO.getError(), "Error description should match 'Validation Error'");
        assertEquals("web request description", errorDTO.getPath(), "Error path should match the mock WebRequest description");

        assertNotNull(errorDTO.getMessages(), "Errors list should not be null");
        assertTrue(errorDTO.getMessages().contains("field: must not be null"), "Errors list should contain the correct violation message");

        assertEquals(1, errorDTO.getMessages().size(), "Errors list should contain exactly one entry");
    }


    @Test
    void testHandleMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "field", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorDTO> response = handler.handleMethodArgumentNotValidException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorDTO errorDTO = response.getBody();
        assertNotNull(errorDTO.getTimestamp(), "Timestamp should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorDTO.getStatus(), "Error status should match BAD_REQUEST");
        assertEquals("Validation Error", errorDTO.getError(), "Error description should match 'Validation Error'");
        assertEquals("web request description", errorDTO.getPath(), "Error path should match the mock WebRequest description");

        assertNotNull(errorDTO.getMessages(), "Errors list should not be null");
        assertFalse(errorDTO.getMessages().isEmpty(), "Errors list should not be empty");
        assertTrue(errorDTO.getMessages().contains("field: must not be null"), "Errors list should contain the correct violation message");
        assertEquals(1, errorDTO.getMessages().size(), "Errors list should contain exactly one entry");
    }

}
