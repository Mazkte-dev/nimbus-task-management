package com.encora.samples.nimbus.task.management.expose.web.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
@Slf4j
public class AuditLogFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Instant startTime = Instant.now();
        
        ServerHttpRequest request = exchange.getRequest();

        String requestId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Request-Id");

        log.info("Request {} - Method: {}, Path: {}, QueryParams: {}",
                requestId,
                request.getMethod(),
                request.getURI().getPath(),
                request.getQueryParams());

        return chain.filter(exchange).doOnSuccess(aVoid -> {
            ServerHttpResponse response = exchange.getResponse();
            Duration duration = Duration.between(startTime, Instant.now());

            log.info("Response - Status: {}, Time Taken: {}ms",
                    response.getStatusCode(),
                    duration.toMillis());
        });
    }
}
