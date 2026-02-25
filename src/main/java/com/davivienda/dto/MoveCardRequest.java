package com.davivienda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para mover una tarjeta a otra lista")
public class MoveCardRequest {

    @NotNull(message = "El ID de la lista destino es requerido")
    @Schema(description = "ID de la lista destino", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long targetListId;

    @Schema(description = "Nueva posición en la lista destino")
    private Integer newPosition;
}
