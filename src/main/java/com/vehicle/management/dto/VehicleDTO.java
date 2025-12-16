package com.vehicle.management.dto;

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
    private String plate;
    private String brand;
    private String color;
    private Integer vehicleYear;
    private BigDecimal price;
}
