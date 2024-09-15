package com.encora.samples.nimbus.task.management.expose.web.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import reactor.test.StepVerifier;

class AuditLogFilterTest {

    private AuditLogFilter auditLogFilter;

    @BeforeEach
    void setUp() {
        auditLogFilter = new AuditLogFilter();
    }

    @Test
    void testAuditFilter() {
        // Prepare test data
        String requestId = "test-request-id";
        HttpMethod httpMethod = HttpMethod.GET;
        String path = "/test/path";
        String queryParams = "param1=value1&param2=value2";
        HttpStatus statusCode = HttpStatus.OK;
        long durationMillis = 100L;

        // Mock ServerWebExchange
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.method(httpMethod, URI.create(path + "?" + queryParams))
                        .header("X-Request-Id", requestId)
                        .build()
        );
        exchange.getResponse().setStatusCode(statusCode);

        // Mock WebFilterChain
        WebFilterChain chain = webFilterChain -> {
            // Simulate response time
            try {
                Thread.sleep(durationMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Mono.empty();
        };

        // Call the filter
        Mono<Void> result = auditLogFilter.filter(exchange, chain);

        // Verify results
        StepVerifier.create(result)
                .expectComplete()
                .verify();


    }
}
