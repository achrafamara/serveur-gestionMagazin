package com.mproduits.GateWay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.server.WebFilter;

import reactor.core.publisher.Mono;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public WebFilter corsFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1) Vérifier si c'est une requête CORS (Origin présent)
            if (isCorsRequest(request)) {
                // 2) Préparer la réponse
                ServerHttpResponse response = exchange.getResponse();
                response.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:4200");
                response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                response.getHeaders().add("Access-Control-Allow-Credentials", "true");

                // 3) Gérer le prévol (OPTIONS)
                if (isPreFlightRequest(request)) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }

            // Continuer la chaîne pour les autres requêtes
            return chain.filter(exchange);
        };
    }

    private boolean isCorsRequest(ServerHttpRequest request) {
        return request.getHeaders().containsKey("Origin");
    }

    private boolean isPreFlightRequest(ServerHttpRequest request) {
        return request.getMethod() == HttpMethod.OPTIONS
                && request.getHeaders().containsKey("Origin")
                && request.getHeaders().containsKey("Access-Control-Request-Method");
    }
}

