package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tarjeta (tarea) en una lista")
public class CardDTO {

    @Schema(description = "ID de la tarjeta")
    private Long id;

    @NotBlank(message = "El título de la tarjeta es requerido")
    @Schema(description = "Título de la tarea", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Descripción detallada")
    private String description;

    @NotNull(message = "La posición es requerida")
    @Schema(description = "Orden dentro de la lista", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer position;

    @Schema(description = "ID de la lista")
    private Long listId;

    @Schema(description = "ID del usuario asignado (opcional)")
    private Long assigneeId;

    @Schema(description = "Nombre del usuario asignado")
    private String assigneeName;

    @Schema(description = "Fecha límite")
    private LocalDate dueDate;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
