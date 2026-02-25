package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para inicio de sesión")
public class LoginRequest {

    @NotBlank(message = "El email es requerido")
    @Email(message = "Email debe ser válido")
    @Schema(description = "Email del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Schema(description = "Contraseña", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}

