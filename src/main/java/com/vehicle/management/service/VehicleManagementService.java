package com.vehicle.management.service;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;

import com.vehicle.management.dto.response.AppResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface VehicleManagementService {
    AppResponseDTO<Page<VehicleDTO>> getAllVehicles(Pageable pageable);
    AppResponseDTO<Page<VehicleDTO>> getVehiclesByFilters(String plate, String brand, Integer year, String color, BigDecimal price, Pageable pageable);
    AppResponseDTO<Page<VehicleDTO>> getVehiclesBypriceRange(BigDecimal minprice, BigDecimal maxprice, Pageable pageable);
    AppResponseDTO<VehicleDTO> getVehicleById(UUID id);
    AppResponseDTO<Page<VehicleBrandReportDTO>> getVehicleBrandReport(Pageable pageable);
    AppResponseDTO<?> addVehicle(VehicleRequestDTO vehicleDTO);
    AppResponseDTO<VehicleDTO> updateVehicle(UUID id, VehicleRequestDTO vehicleDTO);
    AppResponseDTO<VehicleDTO> partialUpdateVehicle(UUID id, VehicleRequestDTO vehicleDTO);
    AppResponseDTO<?> deleteVehicle(UUID id);
}
