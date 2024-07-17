package com.archworker.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {


	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator archworkerRouteConfig(RouteLocatorBuilder builder) {
		String[] generators = {"angulargenerator"};

		RouteLocatorBuilder.Builder routes = builder.routes();

		for (String generator : generators) {
			String path = "/arch/" + generator;
			String eurekaInstanceName = generator.toUpperCase();
			routes.route(generator,
					p -> p.path(path + "/**")
							.filters(
									f -> f.rewritePath(path + "/(?<segment>.*)", "/${segment}")
											.addResponseHeader("X_Response-Time", LocalDateTime.now().toString())
							)
							.uri("lb://" + eurekaInstanceName));
		}

		return routes.build();
	}
}
