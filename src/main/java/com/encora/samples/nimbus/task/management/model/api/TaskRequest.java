package com.encora.samples.nimbus.task.management.model.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TaskRequest {

  private String id;

  private String userId;

  @NotBlank(message = "Title is mandatory")
  private String title;

  private String description;

  @NotBlank(message = "Due date is mandatory")
  private String dueDate;

  @Pattern(regexp = "^(PENDING|IN_PROGRESS|EXPIRED|COMPLETED)$")
  private String status;


}
