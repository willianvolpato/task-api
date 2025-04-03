package com.willian.api.model;

import com.willian.api.model.enums.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Builder;

@Builder
public record TaskModelRequest (
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be less than 255 characters")
    String title,
    @NotBlank(message = "Note is required")
    @Size(min = 1, max = 255, message = "Note must be less than 255 characters")
    String note,
    TaskStatus status
) {}
