package com.encora.samples.nimbus.task.management.services;

import com.encora.samples.nimbus.task.management.exception.TaskException;
import com.encora.samples.nimbus.task.management.mapper.TaskMapper;
import com.encora.samples.nimbus.task.management.model.api.QueryRequest;
import com.encora.samples.nimbus.task.management.model.api.ServiceResponse;
import com.encora.samples.nimbus.task.management.model.api.TaskIDResponse;
import com.encora.samples.nimbus.task.management.model.api.TaskRequest;
import com.encora.samples.nimbus.task.management.model.api.TaskResponse;
import com.encora.samples.nimbus.task.management.model.domain.Task;
import com.encora.samples.nimbus.task.management.repository.TaskRepository;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for managing tasks.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  private final TaskMapper taskMapper;

  /**
   * Creates a new task.
   *
   * @param userId The ID of the user creating the task.
   * @param task The task request object containing the task details.
   * @return A Mono emitting the ID of the created task.
   */
  public Mono<TaskIDResponse> createTask(String userId, TaskRequest task) {
    return taskRepository.findByTitleAndUserId(task.getTitle(), userId)
            .switchIfEmpty(Mono.just(new Task()))
            .flatMap(newTask -> {
              if (Objects.isNull(newTask.getId())){
                task.setUserId(userId);
                return taskRepository.save(taskMapper.createOf(task));
              }
              return Mono.error(new TaskException(HttpStatus.BAD_REQUEST, "Task already exists"));
            })
            .map(newTask -> TaskIDResponse.builder()
                    .id(newTask.getId())
                    .build()
            )
            .onErrorResume(throwable -> {
              if (throwable instanceof TaskException) {
                return Mono.error(throwable);
              }
              return Mono.error(new TaskException(HttpStatus.INTERNAL_SERVER_ERROR,
                      "Error creating task", throwable));
            });
  }

  /**
   * Retrieves a list of tasks for the specified user.
   *
   * @param userId The ID of the user.
   * @param queryRequest The query request object containing the search criteria.
   * @return A Mono emitting a ServiceResponse containing a set of TaskResponse objects.
   */
  public Mono<ServiceResponse<Set<TaskResponse>>> getTasks(String userId, QueryRequest queryRequest) {
    Pageable pageable = PageRequest.of(queryRequest.getPage(),
            queryRequest.getSize(),
            Sort.by(Sort.Direction.fromString(queryRequest.getSortDirection()),
                    queryRequest.getSortBy()));

    Mono<Integer> count = getTaskCount(userId, queryRequest);

    Flux<Task> tasks = taskRepository.findAllByUserId(userId, pageable)
            .filter(task -> !task.getDeleted() &&
                    (StringUtils.isBlank(queryRequest.getStatus()) || queryRequest.getStatus().equals(task.getStatus())))
            .onErrorResume(throwable -> Mono.error(new TaskException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error retrieving tasks", throwable)));

    return Mono.zip(tasks.collectList(), count)
            .map(tuple -> {
              List<Task> tasksList = tuple.getT1();
              Integer totalRecords = tuple.getT2();

              ServiceResponse.PageInfo pageInfo = buildPageInfo(totalRecords, queryRequest);

              return ServiceResponse.queryResponse(
                      tasksList.stream()
                              .map(taskMapper::responseOf)
                              .collect(Collectors.toUnmodifiableSet()),
                      pageInfo);
            });
  }

  /**
   * Retrieves a task by its ID.
   *
   * @param taskId The ID of the task.
   * @return A Mono emitting the TaskResponse object.
   */
  public Mono<TaskResponse> getTaskById(String taskId) {
    return taskRepository.findById(taskId)
            .filter(task -> !task.getDeleted())
            .switchIfEmpty(Mono.error(new TaskException(HttpStatus.NOT_FOUND,
                    "Task not found")))
            .map(taskMapper::withDetailsOf)
            .onErrorResume(throwable -> {
              if (throwable instanceof TaskException) {
                return Mono.error(throwable);
              }
              return Mono.error(new TaskException(HttpStatus.INTERNAL_SERVER_ERROR,
                      "Error searching task"));
            });

  }

  /**
   * Updates an existing task.
   *
   * @param id The ID of the task to update.
   * @param task The task request object containing the updated task details.
   * @return A Mono emitting the updated TaskResponse object.
   */
  public Mono<TaskResponse> updateTask(String id, TaskRequest task) {
    return taskRepository.findById(id)
            .switchIfEmpty(Mono.error(new TaskException(HttpStatus.NOT_FOUND, "Task not found")))
            .flatMap(existingTask -> {
              task.setId(existingTask.getId());
              return taskRepository.save(taskMapper.updateOf(task));
            })
            .map(taskMapper::withDetailsOf)
            .onErrorResume(throwable -> {
              if (throwable instanceof TaskException) {
                return Mono.error(throwable);
              }
              return Mono.error(new TaskException(HttpStatus.INTERNAL_SERVER_ERROR,
                      "Error updating task", throwable));
            });
  }

  /**
   * Deletes a task.
   *
   * @param id The ID of the task to delete.
   * @return A Mono emitting a void value.
   */
  public Mono<Void> deleteTask(String id) {
    return taskRepository.findById(id)
            .switchIfEmpty(Mono.error(new TaskException(HttpStatus.NOT_FOUND, "Task not found")))
            .flatMap(existingTask -> {
              existingTask.setDeleted(Boolean.TRUE);
              existingTask.setLastModifiedDate(LocalDateTime.now());
              return taskRepository.save(existingTask);
            })
            .then()
            .onErrorResume(throwable -> {
              if (throwable instanceof TaskException) {
                return Mono.error(throwable);
              }
              return Mono.error(new TaskException(HttpStatus.INTERNAL_SERVER_ERROR,
                      "Error deleting task"));
            });
  }

  private Mono<Integer> getTaskCount(String userId, QueryRequest queryRequest) {
    if (StringUtils.isBlank(queryRequest.getStatus())) {
      return taskRepository.countByUserIdAndDeleted(userId, false)
              .onErrorResume(throwable -> Mono.error(new TaskException(HttpStatus.INTERNAL_SERVER_ERROR,
                      "Error retrieving tasks", throwable)));
    } else {
      return taskRepository.countByUserIdAndStatusAndDeleted(userId, queryRequest.getStatus(), false)
              .onErrorResume(throwable -> Mono.error(new TaskException(HttpStatus.INTERNAL_SERVER_ERROR,
                      "Error retrieving tasks", throwable)));
    }
  }

  private ServiceResponse.PageInfo buildPageInfo(int totalRecords, QueryRequest queryRequest) {
    int totalPages = (int) Math.ceil((double) totalRecords / queryRequest.getSize());
    return ServiceResponse.PageInfo.builder()
            .totalElements(totalRecords)
            .currentPage(queryRequest.getPage())
            .pageSize(queryRequest.getSize())
            .totalPages(totalPages)
            .numberOfElements(totalRecords > 0 ? Math.min(queryRequest.getSize(), totalRecords - queryRequest.getPage() * queryRequest.getSize()) : 0)
            .build();
  }


}
