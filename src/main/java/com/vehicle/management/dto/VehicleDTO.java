package com.vehicle.management.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class VehicleDTO {

    private UUID id;

    @JsonProperty("placa")
    @JsonAlias("plate")
    private String plate;

    @JsonProperty("marca")
    @JsonAlias("brand")
    private String brand;

    @JsonProperty("cor")
    @JsonAlias("color")
    private String color;

    @JsonProperty("ano")
    @JsonAlias("vehicleYear")
    private Integer vehicleYear;

    @JsonProperty("preco")
    @JsonAlias("price")
    private BigDecimal price;

    @JsonProperty("ativo")
    @JsonAlias("active")
    private boolean active;
}