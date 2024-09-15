package com.encora.samples.nimbus.task.management.expose.web;

import com.encora.samples.nimbus.task.management.model.api.AuthorizationHeader;
import com.encora.samples.nimbus.task.management.model.api.QueryRequest;
import com.encora.samples.nimbus.task.management.model.api.ServiceResponse;
import com.encora.samples.nimbus.task.management.model.api.TaskIDResponse;
import com.encora.samples.nimbus.task.management.model.api.TaskRequest;
import com.encora.samples.nimbus.task.management.model.api.TaskResponse;
import com.encora.samples.nimbus.task.management.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        AuthorizationHeader authorizationHeader = new AuthorizationHeader();
        authorizationHeader.setUserId("user1");
        TaskIDResponse taskIDResponse = TaskIDResponse.builder()
                .id("1")
                .build();

        when(taskService.createTask(anyString(), any(TaskRequest.class))).thenReturn(Mono.just(taskIDResponse));

        Mono<ResponseEntity<TaskIDResponse>> result = taskController.createTask(authorizationHeader, taskRequest);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertEquals("1", response.getBody().getId());
                })
                .verifyComplete();

        verify(taskService, times(1)).createTask("user1", taskRequest);
    }

    @Test
    void testGetTasks() {
        AuthorizationHeader authorizationHeader = new AuthorizationHeader();
        authorizationHeader.setUserId("user1");
        QueryRequest queryRequest = new QueryRequest();
        ServiceResponse<Set<TaskResponse>> serviceResponse = ServiceResponse.<Set<TaskResponse>>builder().build();
        when(taskService.getTasks(anyString(), any(QueryRequest.class))).thenReturn(Mono.just(serviceResponse));

        Mono<ResponseEntity<ServiceResponse>> result = taskController.getTasks(authorizationHeader, queryRequest);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(serviceResponse, response.getBody());
                })
                .verifyComplete();

        verify(taskService, times(1)).getTasks("user1", queryRequest);
    }

    @Test
    void testGetTaskById() {
        AuthorizationHeader authorizationHeader = new AuthorizationHeader();
        authorizationHeader.setUserId("user1");
        String taskId = "1";
        TaskResponse taskResponse = new TaskResponse();
        when(taskService.getTaskById(anyString())).thenReturn(Mono.just(taskResponse));

        Mono<ResponseEntity<TaskResponse>> result = taskController.getTaskById(authorizationHeader, taskId);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(taskResponse, response.getBody());
                })
                .verifyComplete();

        verify(taskService, times(1)).getTaskById(taskId);
    }

    @Test
    void testUpdateTask() {
        AuthorizationHeader authorizationHeader = new AuthorizationHeader();
        authorizationHeader.setUserId("user1");
        String taskId = "1";
        TaskRequest taskRequest = new TaskRequest();
        TaskResponse taskResponse = new TaskResponse();
        when(taskService.updateTask(anyString(), any(TaskRequest.class))).thenReturn(Mono.just(taskResponse));

        Mono<ResponseEntity<TaskResponse>> result = taskController.updateTask(authorizationHeader, taskId, taskRequest);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(taskResponse, response.getBody());
                })
                .verifyComplete();

        verify(taskService, times(1)).updateTask(taskId, taskRequest);
    }

    @Test
    void testDeleteTask() {
        AuthorizationHeader authorizationHeader = new AuthorizationHeader();
        authorizationHeader.setUserId("user1");
        String taskId = "1";
        when(taskService.deleteTask(anyString())).thenReturn(Mono.empty());

        Mono<Void> result = taskController.deleteTask(authorizationHeader, taskId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(taskService, times(1)).deleteTask(taskId);
    }
}
