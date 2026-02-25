package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Proyecto (tablero) tipo Trello")
public class ProjectDTO {

    @Schema(description = "ID del proyecto")
    private Long id;

    @NotBlank(message = "El nombre del proyecto es requerido")
    @Schema(description = "Nombre del proyecto", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Descripción del proyecto")
    private String description;

    @Schema(description = "ID del usuario creador")
    private Long createdById;

    @Schema(description = "Nombre del creador")
    private String createdByName;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;

    @Schema(description = "Listas del tablero (columnas)")
    private List<BoardListDTO> lists;
}
