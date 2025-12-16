package com.vehicle.management.controller;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.service.VehicleManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/veiculos")
public class VehicleManagementController {
    @Autowired
    private VehicleManagementService vehicleManagementService;

    @GetMapping
    public AppResponseDTO<Page<VehicleDTO>> getAllVehicles(
            @RequestParam(required = false) String plate,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal price,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        if (nonNull(brand) || nonNull(year) || nonNull(color) || nonNull(price)) {
            return vehicleManagementService.getVehiclesByFilters(plate, brand, year, color, price, pageable);
        }
        return vehicleManagementService.getAllVehicles(pageable);
    }

    @GetMapping("/preco")
    public AppResponseDTO<Page<VehicleDTO>> getVehiclesByPriceRange(
            @RequestParam @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "Preço mínimo deve ser maior que 0") BigDecimal minPreco,
            @RequestParam @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "Preço máximo deve ser maior que 0") BigDecimal maxPreco,
            @PageableDefault(sort = "price", direction = Sort.Direction.ASC) Pageable pageable){
        return vehicleManagementService.getVehiclesBypriceRange(minPreco, maxPreco, pageable);
    }

    @GetMapping("/{id}")
    public AppResponseDTO<VehicleDTO> getVehicleById(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id) {
        return vehicleManagementService.getVehicleById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponseDTO<?> addVehicle(@RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        return vehicleManagementService.addVehicle(vehicleDTO);
    }

    @PutMapping("/{id}")
    public AppResponseDTO<VehicleDTO> updateVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id, @RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        return vehicleManagementService.updateVehicle(id, vehicleDTO);
    }

    @PatchMapping("/{id}")
    public AppResponseDTO<VehicleDTO> partialUpdateVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id, @RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        return vehicleManagementService.partialUpdateVehicle(id, vehicleDTO);
    }

    @DeleteMapping("/{id}")
    public AppResponseDTO<?> deleteVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id) {
        return vehicleManagementService.deleteVehicle(id);
    }

    @GetMapping("/relatorios/por-marca")
    public AppResponseDTO<Page<VehicleBrandReportDTO>> getVehicleBrandReport(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return vehicleManagementService.getVehicleBrandReport(pageable);
    }
}