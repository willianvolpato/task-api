package com.willian.api.model;

public record AuthRequest (
    String username,
    String password
) {}
