package com.encora.samples.nimbus.task.management.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ServiceResponse<T> {

  @Schema(description = "Total items found in the current query", required = true)
  private PageInfo paging;

  private ErrorResponse error;

  @Schema(description = "Api response data", required = true)
  private T data;

  @Builder
  @Data
  public static class PageInfo {

    @Schema(description = "Total items found in the current query", required = true)
    private Integer totalElements;

    @Schema(description = "Current page size", required = true)
    private Integer pageSize;

    @Schema(description = "Total pages calculated", required = true)
    private Integer totalPages;

    @Schema(description = "Current page number", required = true)
    private Integer currentPage;

    @Schema(description = "Number of items on the current page", required = true)
    private Integer numberOfElements;

  }

  public static <T> ServiceResponse<T> success(T element){
    return ServiceResponse
            .<T>builder()
            .data(element)
            .build();

  }

  public static <T> ServiceResponse<T> failed(ErrorResponse error){
    return ServiceResponse
            .<T>builder()
            .error(error)
            .build();

  }

  public static <T> ServiceResponse<T> queryResponse(T element , PageInfo pageInfo){
    return ServiceResponse
            .<T>builder()
            .paging(pageInfo)
            .data(element)
            .build();

  }


}
