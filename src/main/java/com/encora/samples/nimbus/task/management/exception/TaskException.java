package com.encora.samples.nimbus.task.management.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Data
@Slf4j
public class TaskException extends Throwable {

  private HttpStatus status;

  public TaskException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public TaskException(HttpStatus status, String message, Throwable cause) {
    super(message , cause);
    log.error(cause.getMessage());
    this.status = status;
  }

  public TaskException(String message) {
    super(message);
  }

}
