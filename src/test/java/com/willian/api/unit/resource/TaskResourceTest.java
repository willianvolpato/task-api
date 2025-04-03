package com.willian.api.unit.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willian.api.model.TaskModelRequest;
import com.willian.api.model.TaskModelResponse;
import com.willian.api.model.enums.TaskStatus;
import com.willian.api.persistence.TaskRepository;
import com.willian.api.persistence.entity.TaskEntity;
import com.willian.api.resource.TaskResource;
import com.willian.api.service.TaskService;
import com.willian.api.util.mapper.TaskMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskResourceTest {

    private MockMvc mockMvc;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setupEach() {
        TaskResource taskResource = new TaskResource(taskService);
        mockMvc = MockMvcBuilders.standaloneSetup(taskResource).build();
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        // Given
        final String title = "Title";
        final String note = "Note";
        final TaskStatus status = TaskStatus.TO_DO;
        final LocalDateTime dateTime = LocalDateTime.now().withNano(0);
        TaskModelRequest request = TaskModelRequest.builder()
            .title(title)
            .note(note)
            .status(status)
            .build();
        TaskModelResponse response = TaskModelResponse.builder()
            .id(1L)
            .title(title)
            .note(note)
            .status(status)
            .createdAt(dateTime)
            .updatedAt(null)
            .build();
        TaskEntity taskEntity = TaskEntity.builder()
            .id(1L)
            .title(title)
            .note(note)
            .status(status)
            .createdAt(dateTime)
            .updatedAt(null)
            .build();

        // When
        when(taskMapper.toEntity(any(TaskModelRequest.class))).thenReturn(taskEntity);
        when(taskMapper.toModelResponse(any(TaskEntity.class))).thenReturn(response);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // Then
        mockMvc.perform(
            post("/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper()
                    .writeValueAsString(request)
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.note").value(note))
            .andExpect(jsonPath("$.status").value(status.name()))
            .andExpect(jsonPath("$.createdAt").value(dateTime.toString()))
            .andExpect(jsonPath("$.updatedAt").isEmpty());
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        // Given
        final String title = "Title";
        final String note = "Note";
        final TaskStatus status = TaskStatus.TO_DO;
        final TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        final LocalDateTime dateTime = LocalDateTime.now().withNano(0);
        TaskModelRequest request = TaskModelRequest.builder()
            .status(newStatus)
            .build();
        TaskModelResponse response = TaskModelResponse.builder()
            .id(1L)
            .title(title)
            .note(note)
            .status(newStatus)
            .createdAt(dateTime)
            .updatedAt(dateTime)
            .build();
        TaskEntity taskEntity = TaskEntity.builder()
            .id(1L)
            .title(title)
            .note(note)
            .status(status)
            .createdAt(dateTime)
            .updatedAt(null)
            .build();

        // When
        when(taskMapper.toEntity(any(TaskModelResponse.class))).thenReturn(taskEntity);
        when(taskMapper.toEntity(request)).thenReturn(taskEntity);
        when(taskMapper.toModelResponse(any(TaskEntity.class))).thenReturn(response);
        when(taskRepository.findById(any(long.class))).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // Then
        mockMvc.perform(
                put("/tasks/update/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper()
                        .writeValueAsString(request)
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.note").value(note))
            .andExpect(jsonPath("$.status").value(newStatus.name()))
            .andExpect(jsonPath("$.createdAt").value(dateTime.toString()))
            .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        // Given
        final String title = "Title";
        final String note = "Note";
        final TaskStatus status = TaskStatus.TO_DO;
        final LocalDateTime dateTime = LocalDateTime.now().withNano(0);
        TaskModelResponse response = TaskModelResponse.builder()
            .id(1L)
            .title(title)
            .note(note)
            .status(status)
            .createdAt(dateTime)
            .updatedAt(null)
            .build();
        TaskEntity taskEntity = TaskEntity.builder()
            .id(1L)
            .title(title)
            .note(note)
            .status(status)
            .createdAt(dateTime)
            .updatedAt(null)
            .build();

        // When
        when(taskMapper.toModelResponse(any(TaskEntity.class))).thenReturn(response);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));

        // Then
        mockMvc.perform(get("/tasks/get/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.note").value(note))
            .andExpect(jsonPath("$.status").value(status.name()))
            .andExpect(jsonPath("$.createdAt").value(dateTime.toString()))
            .andExpect(jsonPath("$.updatedAt").isEmpty());
    }
}
