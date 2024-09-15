package com.encora.samples.nimbus.task.management.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Mandatory headers for auditing/monitoring")
public class AuthorizationHeader {

  @JsonProperty("X-Request-Id")
  @NotBlank(message = "Header X-Request-Id is mandatory")
  @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
          message = "Header X-Request-Id must be UUID format" )
  private String requestId;

  @JsonProperty("X-Request-Date")
  @NotBlank(message = "Header X-Request-Date is mandatory")
  private String requestDate;

  @JsonProperty("X-User-Id")
  @NotBlank(message = "Header X-User-Id is mandatory")
  private String userId;

}
