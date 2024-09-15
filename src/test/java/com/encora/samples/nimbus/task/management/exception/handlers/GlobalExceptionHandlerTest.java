package com.encora.samples.nimbus.task.management.exception.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.encora.samples.nimbus.task.management.exception.TaskException;
import com.encora.samples.nimbus.task.management.model.api.ErrorResponse;
import com.encora.samples.nimbus.task.management.model.api.ServiceResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebInputException;

class GlobalExceptionHandlerTest {

  @InjectMocks
  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testHandleErrorResponse_TaskException() {
    TaskException ex = new TaskException(HttpStatus.BAD_REQUEST, "Task error");
    ResponseEntity<ServiceResponse<ErrorResponse>> response = globalExceptionHandler.handleErrorResponse(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Task error", response.getBody().getError().getMessage());
  }

  @Test
  void testHandleErrorResponse_ExpiredJwtException() {
    ExpiredJwtException ex = new ExpiredJwtException(null, null, "Expired JWT");
    ResponseEntity<ServiceResponse<ErrorResponse>> response = globalExceptionHandler.handleErrorResponse(ex);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("Expired JWT", response.getBody().getError().getMessage());
  }

  @Test
  void testHandleRuntimeException() {
    RuntimeException ex = new RuntimeException("Runtime error");
    ResponseEntity<ServiceResponse<ErrorResponse>> response = globalExceptionHandler.handleRuntimeException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Runtime error", response.getBody().getError().getMessage());
  }

  @Test
  void testHandleServerWebInputException() {
    ServerWebInputException ex = new ServerWebInputException("Server Web Input error");
    ResponseEntity<ServiceResponse<ErrorResponse>> response = globalExceptionHandler.handleServerWebInputException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

}
