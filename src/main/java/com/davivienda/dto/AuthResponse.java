package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con JWT")
public class AuthResponse {

    @Schema(description = "Token JWT para autorización en header Authorization: Bearer <token>")
    private String token;

    @Schema(description = "Tipo del token", example = "Bearer")
    private String type;

    @Schema(description = "ID del usuario")
    private Long userId;

    @Schema(description = "Email del usuario")
    private String email;

    @Schema(description = "Rol del usuario", example = "USER")
    private String role;
}

