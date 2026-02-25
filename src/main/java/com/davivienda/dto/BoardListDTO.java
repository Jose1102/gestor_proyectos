package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lista/columna del tablero (ej: Por hacer, En progreso, Hecho)")
public class BoardListDTO {

    @Schema(description = "ID de la lista")
    private Long id;

    @NotBlank(message = "El título de la lista es requerido")
    @Schema(description = "Título de la lista", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotNull(message = "La posición es requerida")
    @Schema(description = "Orden de la lista en el tablero", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer position;

    @Schema(description = "ID del proyecto")
    private Long projectId;

    @Schema(description = "Tarjetas (tareas) de la lista")
    private List<CardDTO> cards;
}
