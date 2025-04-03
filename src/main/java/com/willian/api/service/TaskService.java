package com.willian.api.service;

import com.willian.api.model.TaskModelRequest;
import com.willian.api.model.TaskModelResponse;
import com.willian.api.model.enums.TaskStatus;
import com.willian.api.persistence.TaskRepository;
import com.willian.api.persistence.entity.TaskEntity;
import com.willian.api.util.BeanMerge;
import com.willian.api.util.mapper.TaskMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    @Transactional
    public TaskModelResponse create(TaskModelRequest task) {
        TaskEntity taskEntity = taskMapper.toEntity(task);
        taskEntity.setCreatedAt(LocalDateTime.now());

        if(taskEntity.getStatus() == null)
            taskEntity.setStatus(TaskStatus.TO_DO);

         return Optional.of(taskRepository.save(taskEntity))
            .map(taskMapper::toModelResponse)
            .orElse(null);
    }

    @Transactional
    public TaskModelResponse update(Long id, TaskModelRequest task) {
        TaskEntity originalTask = taskMapper.toEntity(getById(id));

        TaskEntity updatedTask = taskMapper.toEntity(task);
        updatedTask.setId(id);
        updatedTask.setUpdatedAt(LocalDateTime.now());

        updatedTask = BeanMerge.mergeObjects(originalTask, updatedTask);

        return taskMapper.toModelResponse(taskRepository.save(updatedTask));
    }

    @Transactional(readOnly = true)
    public TaskModelResponse getById(Long id) {
        return taskRepository.findById(id)
            .map(taskMapper::toModelResponse)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    @Transactional(readOnly = true)
    public Page<TaskModelResponse> getAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
            .map(taskMapper::toModelResponse);
    }

    @Transactional
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }
}
