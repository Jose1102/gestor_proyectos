package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear una tarjeta (tarea)")
public class CreateCardRequest {

    @NotBlank(message = "El título es requerido")
    @Schema(description = "Título de la tarjeta", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Descripción")
    private String description;

    @NotNull(message = "La posición es requerida")
    @Schema(description = "Posición en la lista", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer position;

    @Schema(description = "ID del usuario asignado (opcional)")
    private Long assigneeId;

    @Schema(description = "Fecha límite (opcional)")
    private LocalDate dueDate;
}
