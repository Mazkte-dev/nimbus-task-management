package com.encora.samples.nimbus.task.management.exception.handlers;

import com.encora.samples.nimbus.task.management.exception.TaskException;
import com.encora.samples.nimbus.task.management.model.api.ErrorResponse;
import com.encora.samples.nimbus.task.management.model.api.ServiceResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TaskException.class)
  public ResponseEntity<ServiceResponse<ErrorResponse>> handleErrorResponse(TaskException ex) {
    return ResponseEntity.status(ex.getStatus())
            .body(ServiceResponse.failed(new ErrorResponse(ex.getStatus().value(),
                    ex.getMessage())));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ServiceResponse<ErrorResponse>> handleErrorResponse(ExpiredJwtException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ServiceResponse.failed(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                    ex.getMessage())));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ServiceResponse<ErrorResponse>> handleRuntimeException(RuntimeException ex) {
    return ResponseEntity.badRequest()
            .body(ServiceResponse.failed(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    ex.getMessage())));
  }

  @ExceptionHandler(ServerWebInputException.class)
  public ResponseEntity<ServiceResponse<ErrorResponse>> handleServerWebInputException(ServerWebInputException ex) {
    return ResponseEntity.badRequest()
            .body(ServiceResponse.failed(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    ex.getMessage())));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<ServiceResponse<ErrorResponse>> handleWebExchangeBindException(WebExchangeBindException ex) {
    return ResponseEntity.badRequest()
            .body(ServiceResponse.failed(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    ex.getMessage())));
  }
}