package com.vehicle.management.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    private String plate;
    @NotBlank(message = "Marca é obrigatória")
    private String brand;
    @NotBlank(message = "Cor é obrigatória")
    private String color;
    @NotBlank(message = "Ano é obrigatório")
    private Integer year;
    @NotBlank(message = "Preço é obrigatório")
    private BigDecimal price;
}
