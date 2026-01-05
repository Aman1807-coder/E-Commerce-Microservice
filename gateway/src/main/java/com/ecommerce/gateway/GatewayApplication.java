package com.ecommerce.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("ecombreaker")
                                .setFallbackUri("forward:/fallback/product")))
                        .uri("lb://PRODUCT-SERVICE"))

                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("ecombreaker")
                                .setFallbackUri("forward:/fallback/user")))
                        .uri("lb://USER-SERVICE"))

                .route("order-service", r -> r
                        .path("/api/orders/**", "/api/cart/**")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("ecombreaker")
                                .setFallbackUri("forward:/fallback/order")))
                        .uri("lb://ORDER-SERVICE"))

                .route("eureka-server", r ->
                         r.path("/eureka/main")
                        .filters(f -> f.setPath("/"))
                        .uri("http://localhost:8761"))

                .route("eureka-server", r ->
                         r.path("/eureka/**")
                        .uri("http://localhost:8761"))

                .build();
    }
}

