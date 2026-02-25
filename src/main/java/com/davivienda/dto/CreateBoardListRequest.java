package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear una lista en un proyecto")
public class CreateBoardListRequest {

    @NotBlank(message = "El título es requerido")
    @Schema(description = "Título de la lista", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotNull(message = "La posición es requerida")
    @Schema(description = "Posición en el tablero", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer position;
}
