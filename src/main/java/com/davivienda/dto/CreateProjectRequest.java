package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear un proyecto")
public class CreateProjectRequest {

    @NotBlank(message = "El nombre del proyecto es requerido")
    @Schema(description = "Nombre del proyecto", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Descripción del proyecto")
    private String description;
}
