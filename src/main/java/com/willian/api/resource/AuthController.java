package com.willian.api.resource;

import com.willian.api.model.AuthRequest;
import com.willian.api.model.AuthResponse;
import com.willian.api.service.AuthService;
import com.willian.api.service.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthController {

    private final AuthService authService;
    private final UserDetailsServiceImpl userService;

    @Operation(summary = "Authenticate a user")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @RequestBody
        AuthRequest request
    ) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(
        @RequestBody
        AuthRequest request
    ) {
        userService.registerUser(request.username(), request.password());
        return ResponseEntity.ok("User registered successfully!");
    }
}
