package com.vehicle.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VehicleFilterDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("placa")
    private String plate;

    @JsonProperty("marca")
    private String brand;

    @JsonProperty("ano")
    private Integer vehicleYear;

    @JsonProperty("cor")
    private String color;

    @JsonProperty("preco")
    private BigDecimal price;

    @JsonProperty("preco_minimo")
    private BigDecimal minPrice;

    @JsonProperty("preco_maximo")
    private BigDecimal maxPrice;
}