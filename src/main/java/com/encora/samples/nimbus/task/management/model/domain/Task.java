package com.encora.samples.nimbus.task.management.model.domain;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("tasks") 
public class Task {

    @Id
    private String id;

    private String userId;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    private String status;

    private LocalDateTime lastModifiedDate;

    private LocalDateTime createdDate;

    private String createdBy;

    private String lastModifiedBy;


    private Boolean deleted;
}