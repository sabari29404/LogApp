package com.example.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
        @NotBlank(message = "Username or email is required") String usernameOrEmail,
        @NotBlank(message = "Password is required")         String password
    ) {}

    public record AuthResponse(
        String token,
        String username,
        String role,
        String expiresIn,
        String message
    ) {}

    public record ErrorResponse(
        String error,
        String message
    ) {}
}
