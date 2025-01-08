package com.mproduits.GateWay.filtre;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomerGatewayFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("----logique avant  d'appeller le prochaine filtre ----");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            System.out.println("Custom Gateway Filter  : traitement  apres la requete");
        }));

    }
}