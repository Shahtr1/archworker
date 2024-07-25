package com.archworker.gatewayserver.unit;

import com.archworker.gatewayserver.filters.FilterUtility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FilterUtilityUnitTests {
    public static final String CORRELATION_ID = "arch-correlation-id";


    private final FilterUtility filterUtility = new FilterUtility();

    @Test
    @DisplayName("Test getCorrelationId returns null when no " + CORRELATION_ID + " is present in header")
    public void testGetCorrelationId_whenNoCorrelationId_returnsNull() {
        // Arrange
        HttpHeaders httpHeaders = new HttpHeaders();

        // Act
        String correlationId = filterUtility.getCorrelationId(httpHeaders);

        // Assert
        assertNull(correlationId);
    }

    @Test
    @DisplayName("Test getCorrelationId returns the correct value when " + CORRELATION_ID + " is present in header")
    public void testGetCorrelationId_whenCorrelationIdPresent_returnsValue() {
        // Arrange
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CORRELATION_ID, "test-correlation-id");

        // Act
        String correlationId = filterUtility.getCorrelationId(httpHeaders);

        // Assert
        assertEquals("test-correlation-id", correlationId);
    }
}
