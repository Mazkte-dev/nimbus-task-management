package com.encora.samples.nimbus.task.management.mapper;

import com.encora.samples.nimbus.task.management.model.api.TaskRequest;
import com.encora.samples.nimbus.task.management.model.api.TaskResponse;
import com.encora.samples.nimbus.task.management.model.domain.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  @Mapping(constant = "PENDING" , target = "status")
  @Mapping(expression = "java(java.time.LocalDateTime.now())" , target = "createdDate")
  @Mapping(source = "dueDate" , target = "dueDate" , dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
  @Mapping(source = "userId" , target = "createdBy")
  @Mapping(constant = "false" , target = "deleted")
  Task createOf(TaskRequest taskRequest);

  @Mapping(source = "dueDate" , target = "dueDate" , dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
  @Mapping(expression = "java(java.time.LocalDateTime.now())" , target = "lastModifiedDate")
  @Mapping(source = "userId" , target = "lastModifiedBy")
  Task updateOf(TaskRequest taskRequest);


  TaskResponse withDetailsOf(Task task);

  @Mapping(source = "id" , target = "id")
  @Mapping(source = "title" , target = "title")
  @Mapping(source = "description" , target = "description")
  @Mapping(target = "dueDate" , ignore = true)
  @Mapping(target = "createdDate" , ignore = true)
  @Mapping(target = "lastModifiedDate" , ignore = true)
  @Mapping(target = "createdBy" , ignore = true)
  @Mapping(target = "lastModifiedBy" , ignore = true)
  @Mapping(target = "deleted" , ignore = true)
  TaskResponse responseOf(Task task);

}
