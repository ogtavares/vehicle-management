package com.vehicle.management.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VehicleRequestDTO {
    @NotBlank(message = "Placa é obrigatória")
    @JsonProperty("placa")
    @JsonAlias("plate")
    private String plate;

    @NotBlank(message = "Marca é obrigatória")
    @JsonProperty("marca")
    @JsonAlias("brand")
    private String brand;

    @NotBlank(message = "Cor é obrigatória")
    @JsonProperty("cor")
    @JsonAlias("color")
    private String color;

    @JsonProperty("ano")
    @JsonAlias("vehicleYear")
    @NotNull(message = "Ano é obrigatório")
    private Integer vehicleYear;

    @JsonProperty("preco")
    @JsonAlias("price")
    @NotNull(message = "Preço é obrigatório")
    private BigDecimal price;
}