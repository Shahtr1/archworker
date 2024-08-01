package com.archworker.gatewayserver.configuration;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.function.Function;

@Configuration
public class GatewayConfig {
    private static Function<PredicateSpec, Buildable<Route>> getBuildableFunction(String generator, String path, String eurekaInstanceName) {
        return p -> p.path(path + "/**")
                .filters(
                        f -> f.rewritePath(path + "/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X_Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(
                                        config -> config.setName(generator + "CircuitBreaker")
                                                .setFallbackUri("forward:/contact-support")
                                )
                )
                .uri("lb://" + eurekaInstanceName);
    }

    @Bean
    public RouteLocator archworkerRouteConfig(RouteLocatorBuilder builder) {
        String[] generators = {"angulargenerator"};

        RouteLocatorBuilder.Builder routes = builder.routes();

        for (String generator : generators) {
            String path = "/arch/" + generator;
            String eurekaInstanceName = generator.toUpperCase();
            routes.route(generator,
                    getBuildableFunction(generator, path, eurekaInstanceName));
        }

        return routes.build();
    }
}
