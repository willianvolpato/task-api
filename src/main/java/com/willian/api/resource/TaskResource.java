package com.willian.api.resource;

import com.willian.api.model.TaskModelRequest;
import com.willian.api.model.TaskModelResponse;
import com.willian.api.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Operations related to tasks management")
public class TaskResource {

    private final TaskService taskService;

    @Operation(summary = "Create a new task")
    @PostMapping("/create")
    public ResponseEntity<TaskModelResponse> persist(
        @RequestBody @Valid
        TaskModelRequest taskModel
    ) {
        TaskModelResponse task = taskService.create(taskModel);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Update an existing task")
    @PutMapping("/update/{id}")
    public ResponseEntity<TaskModelResponse> update(
        @PathVariable
        Long id,
        @RequestBody
        TaskModelRequest taskModel
    ) {
        TaskModelResponse task = taskService.update(id, taskModel);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Get a task by ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<Object> get(
        @PathVariable
        Long id
    ) {
        TaskModelResponse task = taskService.getById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Get all tasks with pagination")
    @GetMapping("/getAll")
    public ResponseEntity<Page<TaskModelResponse>> getAll(
        @ParameterObject
        Pageable pageable
    ) {
        Page<TaskModelResponse> tasks = taskService.getAll(pageable);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Delete a task by ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable
        Long id
    ) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
