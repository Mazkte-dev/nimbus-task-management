package com.encora.samples.nimbus.task.management.model.api;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class QueryRequest {

  private int page = 0;

  private int size = 25;

  @Pattern(regexp = "^(PENDING|IN_PROGRESS|EXPIRED|COMPLETED)$")
  private String status;

  private String sortBy = "dueDate";

  private String sortDirection = "desc";

}
