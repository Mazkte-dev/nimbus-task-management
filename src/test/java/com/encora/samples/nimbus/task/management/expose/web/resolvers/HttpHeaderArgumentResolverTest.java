package com.encora.samples.nimbus.task.management.expose.web.resolvers;

import com.encora.samples.nimbus.task.management.model.api.AuthorizationHeader;
import com.encora.samples.nimbus.task.management.utils.annotations.HttpHeadersMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpHeaderArgumentResolverTest {

    @InjectMocks
    private HttpHeaderArgumentResolver resolver;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new HttpHeaderArgumentResolver(objectMapper);
    }

    @Test
    void testSupportsParameter() throws NoSuchMethodException {
        Method method = TestController.class.getMethod("testMethod", AuthorizationHeader.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    void testResolveArgument() throws Exception {
        // Mock the ServerWebExchange
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Id", "test-user")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Mock the MethodParameter
        Method method = TestController.class.getMethod("testMethod", AuthorizationHeader.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // Mock the objectMapper
        AuthorizationHeader authorizationHeader = new AuthorizationHeader();
        authorizationHeader.setUserId("test-user");
        when(objectMapper.convertValue(any(Map.class), eq(AuthorizationHeader.class))).thenReturn(authorizationHeader);

        // Call the method to test
        Mono<Object> result = resolver.resolveArgument(parameter, mock(BindingContext.class), exchange);

        // Verify the result
        StepVerifier.create(result)
                .assertNext(argument -> {
                    assertTrue(argument instanceof AuthorizationHeader);
                    assertEquals("test-user", ((AuthorizationHeader) argument).getUserId());
                })
                .verifyComplete();

        verify(objectMapper, times(1)).convertValue(any(Map.class), eq(AuthorizationHeader.class));
    }




    private static class TestController {
        public void testMethod(@HttpHeadersMapping AuthorizationHeader authorizationHeader) {
            // Do nothing
        }
    }
}
