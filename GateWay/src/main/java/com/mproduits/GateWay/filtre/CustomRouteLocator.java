package com.mproduits.GateWay.filtre;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomRouteLocator {

    @Bean(name = "customRouteLocatorA")
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route for Product Microservice
                .route("ms-products-route", r -> r.path("/MICROSERVICEPRODUCTS/**")
                        .filters(f -> f
                                .rewritePath("/MICROSERVICEPRODUCTS/(?<remaining>.*)", "/${remaining}")
                                .addRequestHeader("X-request-origin", "Gateway")
                                .filter(new CustomerGatewayFilter()))
                        .uri("lb://microserviceproducts")
                )
                // Route for Commande Microservice
                .route("ms-commandes-route", r -> r.path("/MICROSERVICECOMMANDES/**")
                        .filters(f -> f
                                .rewritePath("/MICROSERVICECOMMANDES/(?<remaining>.*)", "/${remaining}")
                                .addRequestHeader("X-request-origin", "Gateway")
                                .filter(new CustomerGatewayFilter()))
                        .uri("lb://microservicecommandes")
                )
                // Route for Client Microservice
                .route("ms-clients-route", r -> r.path("/MICROSERVICECLIENTS/**")
                        .filters(f -> f
                                .rewritePath("/MICROSERVICECLIENTS/(?<remaining>.*)", "/${remaining}")
                                .addRequestHeader("X-request-origin", "Gateway")
                                .filter(new CustomerGatewayFilter())
                        )
                        .uri("lb://microserviceclients")  // Assurez-vous que "microserviceclients" est le nom correct de votre microservice dans le registre
                )
                .build();
    }
}

