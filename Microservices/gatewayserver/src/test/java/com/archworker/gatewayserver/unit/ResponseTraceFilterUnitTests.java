package com.archworker.gatewayserver.unit;

import com.archworker.gatewayserver.filters.FilterUtility;
import com.archworker.gatewayserver.filters.ResponseTraceFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResponseTraceFilterUnitTests {

    private final String correlationId = java.util.UUID.randomUUID().toString();

    @Mock
    private FilterUtility filterUtility;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private HttpHeaders requestHeaders;

    @Mock
    private HttpHeaders responseHeaders;

    @InjectMocks
    private ResponseTraceFilter responseTraceFilter;

    @Captor
    private ArgumentCaptor<String> correlationIdCaptor;

    @Test
    @DisplayName("Test postGlobalFilter with correlation-id present in request")
    void testPostGlobalFilter_whenCorrelationIdPresentInRequest_shouldSetInResponse() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(requestHeaders);
        when(response.getHeaders()).thenReturn(responseHeaders);
        when(filterUtility.getCorrelationId(requestHeaders)).thenReturn(correlationId);

        GlobalFilter filter = responseTraceFilter.postGlobalFilter();

        // Act
        filter.filter(exchange, chain -> Mono.empty()).block(); // block waits for filter processing to finish

        // Assert
        verify(filterUtility).getCorrelationId(requestHeaders);
        if (!responseHeaders.containsKey(FilterUtility.CORRELATION_ID)) {
            verify(responseHeaders).add(FilterUtility.CORRELATION_ID, correlationId);
        } else {
            assertEquals(correlationId, responseHeaders.getFirst(FilterUtility.CORRELATION_ID));
        }
    }

    @Test
    @DisplayName("Test postGlobalFilter without correlation-id in request")
    void testPostGlobalFilter_whenNoCorrelationIdInRequest_shouldNotChangeResponseHeaders() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(requestHeaders);
        when(response.getHeaders()).thenReturn(responseHeaders);
        when(filterUtility.getCorrelationId(requestHeaders)).thenReturn(null);

        GlobalFilter filter = responseTraceFilter.postGlobalFilter();

        // Act
        filter.filter(exchange, chain -> Mono.empty()).block(); // block waits for filter processing to finish

        // Assert
        verify(filterUtility).getCorrelationId(requestHeaders);
        verify(responseHeaders, never()).add(eq(FilterUtility.CORRELATION_ID), anyString());
    }
}
