package com.encora.samples.nimbus.task.management.repository;

import com.encora.samples.nimbus.task.management.model.domain.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {

    Mono<Task> findByTitleAndUserId(String title , String userId);

    Mono<Integer> countByUserIdAndDeleted(String userId , boolean deleted);

    Mono<Integer> countByUserIdAndStatusAndDeleted(String userId , String status, boolean deleted);

    Flux<Task> findAllByUserId(String userId , Pageable pageable);
}