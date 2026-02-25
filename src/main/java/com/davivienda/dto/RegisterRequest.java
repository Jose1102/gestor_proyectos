package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para registro de usuario")
public class RegisterRequest {

    @NotBlank(message = "El email es requerido")
    @Email(message = "Email debe ser válido")
    @Schema(description = "Email del usuario", example = "usuario@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String nombre;
}

