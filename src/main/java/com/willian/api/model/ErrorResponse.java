package com.willian.api.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

public record ErrorResponse (
    int status,
    String error,
    List<String> message,
    LocalDateTime timestamp
) {
    public ErrorResponse(HttpStatus status, List<String> message) {
        this(
            status.value(),
            status.getReasonPhrase(),
            message,
            LocalDateTime.now()
        );
    }

    public ErrorResponse(HttpStatus status, String message) {
        this(
            status.value(),
            status.getReasonPhrase(),
            List.of(message),
            LocalDateTime.now()
        );
    }
}
