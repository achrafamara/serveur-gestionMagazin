package com.mproduits.GateWay.filtre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Slf4j
@Component
public class MyGlobalLogFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().toString();
        log.info("requete interceptéé  ! URL :{}",url);
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return -1 ;  // order de priorité des filtes
    }
}