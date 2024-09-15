package com.encora.samples.nimbus.task.management.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponse {

  private String id;

  private String userId;

  private String title;

  private String description;

  private LocalDate dueDate;

  private String status;

  private LocalDateTime lastModifiedDate;

  private LocalDateTime createdDate;

  private String createdBy;

  private String lastModifiedBy;

  private Boolean deleted;

}
