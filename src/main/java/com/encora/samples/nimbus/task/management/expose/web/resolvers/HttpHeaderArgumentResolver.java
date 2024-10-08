package com.encora.samples.nimbus.task.management.expose.web.resolvers;

import com.encora.samples.nimbus.task.management.utils.annotations.HttpHeadersMapping;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class HttpHeaderArgumentResolver implements HandlerMethodArgumentResolver {

  private ObjectMapper mapper;

  public HttpHeaderArgumentResolver(ObjectMapper mapper) {
    super();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    this.mapper = mapper;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(HttpHeadersMapping.class);
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter,
                                      BindingContext bindingContext,
                                      ServerWebExchange exchange) {

    return Mono.create(sink -> {

      Map<String, String> headersMap = new HashMap<>();

      exchange.getRequest().getHeaders()
              .forEach((s, strings) -> headersMap.put(s, strings.get(0)));

      Map<String, Object> headers = this.convertFromHeaders(headersMap);

      Object obj = mapper.convertValue(headers, parameter.getParameterType());

      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();

      Set<ConstraintViolation<Object>> violations = validator.validate(obj);
      if (!violations.isEmpty()) {
        sink.error(new ConstraintViolationException(violations));
      } else {
        sink.success(obj);
      }

    });
  }

  private Map<String, Object> convertFromHeaders(Map<String, String> headers) {
    Map<String, Object> headersMap = new HashMap<>();
    headers.entrySet().forEach(entry -> headersMap.put(entry.getKey(), entry.getValue()));
    return headersMap;
  }

}