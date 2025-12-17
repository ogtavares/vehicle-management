package com.vehicle.management.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(
        name = "VehiclePatchRequest",
        description = "DTO para atualização parcial de um veículo. Apenas os campos informados serão atualizados."
)
public class VehiclePatchRequestDTO {
    @Schema(
            description = "Placa do veículo",
            example = "ABC1234",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("placa")
    @JsonAlias("plate")
    private String plate;

    @Schema(
            description = "Marca do veículo",
            example = "Toyota",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("marca")
    @JsonAlias("brand")
    private String brand;

    @Schema(
            description = "Cor do veículo",
            example = "Preto",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("cor")
    @JsonAlias("color")
    private String color;

    @Schema(
            description = "Ano de fabricação do veículo",
            example = "2022",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("ano")
    @JsonAlias("vehicleYear")
    private Integer vehicleYear;

    @Schema(
            description = "Preço do veículo",
            example = "75000.00",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("preco")
    @JsonAlias("price")
    private BigDecimal price;
}