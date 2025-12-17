package com.vehicle.management.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(
        name = "VehicleRequest",
        description = "DTO utilizado para criação ou atualização de veículos"
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VehicleRequestDTO {
    @Schema(
            description = "Placa do veículo",
            example = "ABC1234",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Placa é obrigatória")
    @JsonProperty("placa")
    @JsonAlias("plate")
    private String plate;

    @Schema(
            description = "Marca do veículo",
            example = "Toyota",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Marca é obrigatória")
    @JsonProperty("marca")
    @JsonAlias("brand")
    private String brand;

    @Schema(
            description = "Cor do veículo",
            example = "Preto",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Cor é obrigatória")
    @JsonProperty("cor")
    @JsonAlias("color")
    private String color;

    @Schema(
            description = "Ano de fabricação do veículo",
            example = "2022",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("ano")
    @JsonAlias("vehicleYear")
    @NotNull(message = "Ano é obrigatório")
    private Integer vehicleYear;

    @Schema(
            description = "Preço do veículo em reais",
            example = "85000.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("preco")
    @JsonAlias("price")
    @NotNull(message = "Preço é obrigatório")
    private BigDecimal price;
}
