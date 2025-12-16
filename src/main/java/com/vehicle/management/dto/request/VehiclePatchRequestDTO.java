package com.vehicle.management.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VehiclePatchRequestDTO {
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
}
