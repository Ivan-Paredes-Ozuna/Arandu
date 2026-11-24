package com.arandu.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank(message = "Ingresá tu nombre completo") String fullName,
            @NotBlank(message = "Ingresá un correo electrónico válido")
            @Email(message = "Ingresá un correo electrónico válido") String email,
            @NotBlank(message = "Ingresá una contraseña")
            @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password) {
    }

    public record LoginRequest(
            @NotBlank(message = "Ingresá un correo electrónico válido")
            @Email(message = "Ingresá un correo electrónico válido") String email,
            @NotBlank(message = "Ingresá tu contraseña") String password) {
    }

    public record AuthResponse(String token, UserResponse user) {
    }

    public record UserResponse(Long id, String fullName, String email) {
    }
}
