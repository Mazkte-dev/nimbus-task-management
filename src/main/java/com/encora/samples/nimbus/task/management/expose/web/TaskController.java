package com.encora.samples.nimbus.task.management.expose.web;

import com.encora.samples.nimbus.task.management.model.api.AuthorizationHeader;
import com.encora.samples.nimbus.task.management.model.api.QueryRequest;
import com.encora.samples.nimbus.task.management.model.api.ServiceResponse;
import com.encora.samples.nimbus.task.management.model.api.TaskIDResponse;
import com.encora.samples.nimbus.task.management.model.api.TaskRequest;
import com.encora.samples.nimbus.task.management.model.api.TaskResponse;
import com.encora.samples.nimbus.task.management.services.TaskService;
import com.encora.samples.nimbus.task.management.utils.annotations.HttpHeadersMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing tasks.
 *
 * This controller provides endpoints for creating, retrieving, updating, and deleting tasks.
 * It uses the `X-User-Id` header to identify the user making the request.
 */
@Tag(name = "Task", description = "Gestiona parámetros de búsqueda")
@RestController
@RequestMapping("${application.api.path}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new task for the authenticated user.
     *
     * @param authorizationHeader The authorization header containing the user ID.
     * @param task The task request object containing the task details.
     * @return A Mono emitting a ResponseEntity with the ID of the created task.
     */
    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ServiceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Request invalid", content = @Content)
    })
    @PostMapping
    public Mono<ResponseEntity<TaskIDResponse>> createTask(@HttpHeadersMapping AuthorizationHeader authorizationHeader,
                                                           @RequestBody @Valid TaskRequest task) {
        return taskService.createTask(authorizationHeader.getUserId(), task)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(response));
    }

    /**
     * Retrieves a list of tasks for the authenticated user.
     *
     * @param authorizationHeader The authorization header containing the user ID.
     * @param queryRequest The query request object containing the search criteria.
     * @return A Mono emitting a ResponseEntity with a ServiceResponse containing a set of TaskResponse objects.
     */
    @GetMapping
    public Mono<ResponseEntity<ServiceResponse>> getTasks(@HttpHeadersMapping AuthorizationHeader authorizationHeader,
                                                          QueryRequest queryRequest) {
        return taskService.getTasks(authorizationHeader.getUserId(),
                        queryRequest)
                .map(response -> ResponseEntity.ok(response));
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param authorizationHeader The authorization header containing the user ID.
     * @param id The ID of the task.
     * @return A Mono emitting a ResponseEntity with the TaskResponse object.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> getTaskById(@HttpHeadersMapping AuthorizationHeader authorizationHeader,
                                                          @PathVariable String id) {
        return taskService.getTaskById(id)
                .map(response -> ResponseEntity.ok(response));
    }

    /**
     * Updates an existing task.
     *
     * @param authorizationHeader The authorization header containing the user ID.
     * @param id The ID of the task to update.
     * @param task The task request object containing the updated task details.
     * @return A Mono emitting a ResponseEntity with the updated TaskResponse object.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> updateTask(@HttpHeadersMapping AuthorizationHeader authorizationHeader,
                                                         @PathVariable String id,
                                                         @RequestBody TaskRequest task) {
        return taskService.updateTask(id, task)
                .map(response -> ResponseEntity.ok(response));
    }

    /**
     * Deletes a task.
     *
     * @param authorizationHeader The authorization header containing the user ID.
     * @param id The ID of the task to delete.
     * @return A Mono emitting a void value.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTask(@HttpHeadersMapping AuthorizationHeader authorizationHeader,
                                 @PathVariable String id) {
        return taskService.deleteTask(id);
    }
}
