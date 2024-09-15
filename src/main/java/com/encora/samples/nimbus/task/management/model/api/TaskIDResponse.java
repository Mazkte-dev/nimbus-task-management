package com.encora.samples.nimbus.task.management.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskIDResponse {

  @Schema(description = "ID of the task created", required = true)
  private String id;

}
