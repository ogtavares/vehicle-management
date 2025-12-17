package com.vehicle.management.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(description = "Representa um veículo retornado pela API")
public class VehicleDTO {
    @Schema(
            description = "Identificador único do veículo",
            example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
    )
    private UUID id;

    @Schema(description = "Placa do veículo", example = "ABC1234")
    @JsonProperty("placa")
    @JsonAlias("plate")
    private String plate;

    @Schema(description = "Marca do veículo", example = "Toyota")
    @JsonProperty("marca")
    @JsonAlias("brand")
    private String brand;

    @Schema(description = "Cor do veículo", example = "Preto")
    @JsonProperty("cor")
    @JsonAlias("color")
    private String color;

    @Schema(description = "Ano de fabricação do veículo", example = "2022")
    @JsonProperty("ano")
    @JsonAlias("vehicleYear")
    private Integer vehicleYear;

    @Schema(description = "Preço do veículo em reais", example = "80000.00")
    @JsonProperty("preco")
    @JsonAlias("price")
    private BigDecimal price;

    @Schema(description = "Indica se o veículo está ativo", example = "true")
    @JsonProperty("ativo")
    @JsonAlias("active")
    private boolean active;
}
