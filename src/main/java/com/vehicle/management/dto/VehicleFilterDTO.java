package com.vehicle.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleFilterDTO {
    private UUID id;
    private String plate;
    private String brand;
    private Integer vehicleYear;
    private String color;
    private BigDecimal price;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
