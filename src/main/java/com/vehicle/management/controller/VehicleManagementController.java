package com.vehicle.management.controller;

import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.service.VehicleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

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
            @RequestParam(required = false) BigDecimal price) {
        if (brand != null || year != null || color != null) {
            return vehicleManagementService.getVehiclesByFilters(plate, brand, year, color, price);
        }
        return vehicleManagementService.getAllVehicles();
    }

    @GetMapping("/preco")
    public AppResponseDTO<Page<VehicleDTO>> getVehiclesByPriceRange(
            @RequestParam BigDecimal minPreco,
            @RequestParam BigDecimal maxPreco) {
        return vehicleManagementService.getVehiclesBypriceRange(minPreco, maxPreco);
    }

    @GetMapping("/{id}")
    public AppResponseDTO<VehicleDTO> getVehicleById(@PathVariable UUID id) {
        return vehicleManagementService.getVehicleById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponseDTO<?> addVehicle(@RequestBody VehicleDTO vehicleDTO) {
        return vehicleManagementService.addVehicle(vehicleDTO);
    }

    @PutMapping("/{id}")
    public AppResponseDTO<VehicleDTO> updateVehicle(@PathVariable UUID id, @RequestBody VehicleDTO vehicleDTO) {
        vehicleDTO.setId(id);
        return vehicleManagementService.updateVehicle(vehicleDTO);
    }

    @PatchMapping("/{id}")
    public AppResponseDTO<VehicleDTO> partialUpdateVehicle(@PathVariable UUID id, @RequestBody VehicleDTO vehicleDTO) {
        vehicleDTO.setId(id);
        return vehicleManagementService.partialUpdateVehicle(vehicleDTO);
    }

    @DeleteMapping("/{id}")
    public AppResponseDTO<?> deleteVehicle(@PathVariable UUID id) {
        return vehicleManagementService.deleteVehicle(id);
    }

    @GetMapping("/relatorios/por-marca")
    public AppResponseDTO<?> getVehicleBrandReport() {
        return vehicleManagementService.getVehicleBrandReport();
    }
}