package com.archworker.gatewayserver.unit.configuration;

import com.archworker.gatewayserver.configuration.GatewayConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GatewayConfigUnitTests {

    @Mock
    private RouteLocatorBuilder routeLocatorBuilder;

    @Mock
    private Builder routesBuilder;

    @Mock
    private PredicateSpec predicateSpec;

    @Mock
    private BooleanSpec booleanSpec;

    @Mock
    private UriSpec uriSpec;

    @Mock
    private GatewayFilterSpec gatewayFilterSpec;

    @Mock
    private Buildable<Route> routeBuildable;

    @InjectMocks
    private GatewayConfig gatewayConfig;

    @Test
    @DisplayName("Test route Configuration")
    public void testRouteConfiguration() {
        // Arrange
        when(routeLocatorBuilder.routes()).thenReturn(routesBuilder);
        when(routesBuilder.route(any(String.class), any(Function.class))).thenAnswer(invocation -> {
            String routeId = invocation.getArgument(0);
            Function<PredicateSpec, Buildable<Route>> fn = invocation.getArgument(1);
            Buildable<Route> builder = fn.apply(predicateSpec);
            assertNotNull(builder);
            assertEquals("angulargenerator", routeId);
            return routesBuilder;
        });

        when(predicateSpec.path(any(String.class))).thenReturn(booleanSpec);
        when(booleanSpec.filters(any(Function.class))).thenAnswer(invocation -> {
            Function<GatewayFilterSpec, UriSpec> fn = invocation.getArgument(0);
            UriSpec resUriSpec = fn.apply(gatewayFilterSpec);
            assertNotNull(resUriSpec);
            return uriSpec;
        });

        when(uriSpec.uri(any(String.class))).thenReturn(routeBuildable);

        when(gatewayFilterSpec.rewritePath(any(String.class), any(String.class))).thenReturn(gatewayFilterSpec);
        when(gatewayFilterSpec.addResponseHeader(any(String.class), any(String.class))).thenReturn(gatewayFilterSpec);
        when(gatewayFilterSpec.circuitBreaker(any(Consumer.class))).thenReturn(gatewayFilterSpec);
        when(routesBuilder.build()).thenReturn(mock(RouteLocator.class));


        // Act
        RouteLocator routeLocator = gatewayConfig.archworkerRouteConfig(routeLocatorBuilder);

        // Assert`
        assertNotNull(routeLocator);
        verify(routeLocatorBuilder, times(1)).routes();
        verify(routesBuilder, times(1)).route(eq("angulargenerator"), any(Function.class));
        verify(predicateSpec, times(1)).path(eq("/arch/angulargenerator/**"));
        verify(booleanSpec, times(1)).filters(any(Function.class));
        verify(uriSpec, times(1)).uri(eq("lb://ANGULARGENERATOR"));
    }
}
