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
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

  @InjectMocks
  private TaskService taskService;

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private TaskMapper taskMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateTaskSuccess() {
    TaskRequest taskRequest = new TaskRequest();
    taskRequest.setTitle("Test Task");
    taskRequest.setDescription("Test Description");
    taskRequest.setStatus("PENDING");

    Task task = new Task();
    task.setId("1");
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setStatus("PENDING");
    task.setUserId("user1");

    when(taskRepository.findByTitleAndUserId(anyString(), anyString())).thenReturn(Mono.empty());
    when(taskMapper.createOf(any(TaskRequest.class))).thenReturn(task);
    when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));

    Mono<TaskIDResponse> result = taskService.createTask("user1", taskRequest);

    StepVerifier.create(result)
            .assertNext(taskIDResponse -> {
              assertEquals("1", taskIDResponse.getId());
            })
            .verifyComplete();

    verify(taskRepository, times(1)).findByTitleAndUserId(anyString(), anyString());
    verify(taskMapper, times(1)).createOf(any(TaskRequest.class));
    verify(taskRepository, times(1)).save(any(Task.class));
  }

  @Test
  void testCreateTaskAlreadyExists() {
    TaskRequest taskRequest = new TaskRequest();
    taskRequest.setTitle("Test Task");
    taskRequest.setDescription("Test Description");
    taskRequest.setStatus("PENDING");

    Task existingTask = new Task();
    existingTask.setId("1");
    existingTask.setTitle("Test Task");
    existingTask.setDescription("Test Description");
    existingTask.setStatus("PENDING");
    existingTask.setUserId("user1");

    when(taskRepository.findByTitleAndUserId(anyString(), anyString())).thenReturn(Mono.just(existingTask));

    Mono<TaskIDResponse> result = taskService.createTask("user1", taskRequest);

    StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof TaskException &&
                    throwable.getMessage().equals("Task already exists"))
            .verify();

    verify(taskRepository, times(1)).findByTitleAndUserId(anyString(), anyString());
    verify(taskMapper, never()).createOf(any(TaskRequest.class));
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  void testGetTasksSuccess() {
    String userId = "user1";
    QueryRequest queryRequest = new QueryRequest();

    queryRequest.setPage(0);
    queryRequest.setSize(10);
    queryRequest.setSortBy("title");
    queryRequest.setSortDirection("asc");

    Task task1 = new Task();
    task1.setId("1");
    task1.setTitle("Task 1");
    task1.setUserId(userId);
    task1.setDeleted(false);

    Task task2 = new Task();
    task2.setId("2");
    task2.setTitle("Task 2");
    task2.setUserId(userId);
    task2.setDeleted(false);

    when(taskRepository.findAllByUserId(anyString(), any())).thenReturn(Flux.just(task1, task2));
    when(taskRepository.countByUserIdAndDeleted(anyString(), anyBoolean())).thenReturn(Mono.just(2));
    when(taskMapper.responseOf(any(Task.class))).thenReturn(new TaskResponse());

    Mono<ServiceResponse<Set<TaskResponse>>> result = taskService.getTasks(userId, queryRequest);

    StepVerifier.create(result)
            .assertNext(serviceResponse -> {
              assertNotNull(serviceResponse.getData());
              assertEquals(1, serviceResponse.getData().size());
              assertNotNull(serviceResponse.getPaging());
              assertEquals(2, serviceResponse.getPaging().getTotalElements());
            })
            .verifyComplete();

    verify(taskRepository, times(1)).findAllByUserId(anyString(), any());
    verify(taskRepository, times(1)).countByUserIdAndDeleted(anyString(), anyBoolean());
    verify(taskMapper, times(2)).responseOf(any(Task.class));
  }

  @Test
  void testGetTaskByIdSuccess() {
    String taskId = "1";

    Task task = new Task();
    task.setId(taskId);
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setStatus("PENDING");
    task.setUserId("user1");
    task.setDeleted(false);


    when(taskRepository.findById(taskId)).thenReturn(Mono.just(task));
    when(taskMapper.withDetailsOf(any(Task.class))).thenReturn(new TaskResponse());

    Mono<TaskResponse> result = taskService.getTaskById(taskId);

    StepVerifier.create(result)
            .assertNext(taskResponse -> {
              assertNotNull(taskResponse);
            })
            .verifyComplete();

    verify(taskRepository, times(1)).findById(taskId);
    verify(taskMapper, times(1)).withDetailsOf(any(Task.class));
  }

  @Test
  void testGetTaskByIdNotFound() {
    String taskId = "1";

    when(taskRepository.findById(taskId)).thenReturn(Mono.empty());

    Mono<TaskResponse> result = taskService.getTaskById(taskId);

    StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof TaskException &&
                    throwable.getMessage().equals("Task not found"))
            .verify();

    verify(taskRepository, times(1)).findById(taskId);
    verify(taskMapper, never()).withDetailsOf(any(Task.class));
  }

  @Test
  void testUpdateTaskSuccess() {
    String taskId = "1";

    TaskRequest taskRequest = new TaskRequest();
    taskRequest.setTitle("Updated Task");
    taskRequest.setDescription("Updated Description");
    taskRequest.setStatus("IN_PROGRESS");

    Task existingTask = new Task();
    existingTask.setId(taskId);
    existingTask.setTitle("Test Task");
    existingTask.setDescription("Test Description");
    existingTask.setStatus("PENDING");
    existingTask.setUserId("user1");

    Task updatedTask = new Task();
    updatedTask.setId(taskId);
    updatedTask.setTitle("Updated Task");
    updatedTask.setDescription("Updated Description");
    updatedTask.setStatus("IN_PROGRESS");
    updatedTask.setUserId("user1");

    when(taskRepository.findById(taskId)).thenReturn(Mono.just(existingTask));
    when(taskMapper.updateOf(any(TaskRequest.class))).thenReturn(updatedTask);
    when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(updatedTask));
    when(taskMapper.withDetailsOf(any(Task.class))).thenReturn(new TaskResponse());

    Mono<TaskResponse> result = taskService.updateTask(taskId, taskRequest);

    StepVerifier.create(result)
            .assertNext(taskResponse -> {
              assertNotNull(taskResponse);
            })
            .verifyComplete();

    verify(taskRepository, times(1)).findById(taskId);
    verify(taskMapper, times(1)).updateOf(any(TaskRequest.class));
    verify(taskRepository, times(1)).save(any(Task.class));
    verify(taskMapper, times(1)).withDetailsOf(any(Task.class));
  }

  @Test
  void testUpdateTaskNotFound() {
    String taskId = "1";

    TaskRequest taskRequest = new TaskRequest();
    taskRequest.setTitle("Updated Task");
    taskRequest.setDescription("Updated Description");
    taskRequest.setStatus("IN_PROGRESS");

    when(taskRepository.findById(taskId)).thenReturn(Mono.empty());

    Mono<TaskResponse> result = taskService.updateTask(taskId, taskRequest);

    StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof TaskException &&
                    throwable.getMessage().equals("Task not found"))
            .verify();

    verify(taskRepository, times(1)).findById(taskId);
    verify(taskMapper, never()).updateOf(any(TaskRequest.class));
    verify(taskRepository, never()).save(any(Task.class));
    verify(taskMapper, never()).withDetailsOf(any(Task.class));
  }

  @Test
  void testDeleteTaskSuccess() {
    String taskId = "1";

    Task existingTask = new Task();
    existingTask.setId(taskId);
    existingTask.setTitle("Test Task");
    existingTask.setDescription("Test Description");
    existingTask.setStatus("PENDING");
    existingTask.setUserId("user1");

    when(taskRepository.findById(taskId)).thenReturn(Mono.just(existingTask));
    when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(existingTask));

    Mono<Void> result = taskService.deleteTask(taskId);

    StepVerifier.create(result)
            .verifyComplete();

    verify(taskRepository, times(1)).findById(taskId);
    verify(taskRepository, times(1)).save(any(Task.class));
  }

  @Test
  void testDeleteTaskNotFound() {
    String taskId = "1";

    when(taskRepository.findById(taskId)).thenReturn(Mono.empty());

    Mono<Void> result = taskService.deleteTask(taskId);

    StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof TaskException &&
                    throwable.getMessage().equals("Task not found"))
            .verify();

    verify(taskRepository, times(1)).findById(taskId);
    verify(taskRepository, never()).save(any(Task.class));
  }

}
