package com.willian.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.willian.api.model.enums.TaskStatus;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record TaskModelResponse (
    Long id,
    String title,
    String note,
    TaskStatus status,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt
){}
