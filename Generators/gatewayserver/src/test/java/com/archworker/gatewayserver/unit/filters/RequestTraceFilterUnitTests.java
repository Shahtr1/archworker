package com.archworker.gatewayserver.unit.filters;

import com.archworker.gatewayserver.filters.FilterUtility;
import com.archworker.gatewayserver.filters.RequestTraceFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestTraceFilterUnitTests {

    private final String correlationId = java.util.UUID.randomUUID().toString();

    @Mock
    private FilterUtility filterUtility;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private ServerHttpRequest request;

    @InjectMocks
    private RequestTraceFilter requestTraceFilter;

    @Captor
    private ArgumentCaptor<String> correlationIdCaptor;


    @Test
    @DisplayName("Test filter with no correlation-id")
    void testFilter_whenNoCorrelationId_returnCorrelationId() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getRequest().getHeaders()).thenReturn(httpHeaders);
        when(filterUtility.getCorrelationId(httpHeaders)).thenReturn(null);
        when(filterUtility.setCorrelationId(any(ServerWebExchange.class), correlationIdCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0)); // returns the first argument back. i.e., exchange
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        requestTraceFilter.filter(exchange, chain).block(); // block waits for filter processing to finish

        // Assert
        verify(filterUtility).setCorrelationId(eq(exchange), anyString());
        verify(chain).filter(exchange);

        String generatedCorrelationId = correlationIdCaptor.getValue();
        assertNotNull(generatedCorrelationId, "Generated correlation ID should not be null");
        assertEquals(36, generatedCorrelationId.length(), "Generated correlation ID should be 36 characters long (UUID format)");
    }

    @Test
    @DisplayName("Test filter with correlation-id")
    void testFilter_whenCorrelationIdPresent_shouldReturnSameCorrelationId() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(httpHeaders);
        when(filterUtility.getCorrelationId(httpHeaders)).thenReturn(correlationId);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        requestTraceFilter.filter(exchange, chain).block();

        // Assert
        verify(filterUtility, never()).setCorrelationId(any(ServerWebExchange.class), anyString());
        verify(chain).filter(exchange);
        assertEquals(correlationId, filterUtility.getCorrelationId(httpHeaders));

    }

}
