package com.encora.samples.nimbus.task.management.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

  private Integer status;

  private String message;
}