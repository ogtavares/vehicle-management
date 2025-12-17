package com.vehicle.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(description = "Relatório agregando veículos por marca")
public class VehicleBrandReportDTO {
    @Schema(description = "Marca do veículo", example = "Toyota")
    @JsonProperty("marca")
    private String brand;

    @Schema(description = "Quantidade de veículos da marca", example = "12")
    @JsonProperty("quantidade")
    private Long count;
}
