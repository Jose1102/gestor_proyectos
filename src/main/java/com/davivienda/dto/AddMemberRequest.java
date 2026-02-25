package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para añadir un miembro al proyecto por email")
public class AddMemberRequest {

    @NotBlank(message = "El email es requerido")
    @Email(message = "Email debe ser válido")
    @Schema(description = "Email del usuario a añadir", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
