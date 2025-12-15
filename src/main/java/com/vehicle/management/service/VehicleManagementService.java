package com.vehicle.management.service;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface VehicleManagementService {
    AppResponseDTO<Page<VehicleDTO>> getAllVehicles();
    AppResponseDTO<Page<VehicleDTO>> getVehiclesByFilters(String plate, String brand, Integer year, String color, BigDecimal price);
    AppResponseDTO<Page<VehicleDTO>> getVehiclesBypriceRange(BigDecimal minprice, BigDecimal maxprice);
    AppResponseDTO<VehicleDTO> getVehicleById(UUID id);
    AppResponseDTO<List<VehicleBrandReportDTO>> getVehicleBrandReport();
    AppResponseDTO<?> addVehicle(VehicleDTO vehicleDTO);
    AppResponseDTO<VehicleDTO> updateVehicle(VehicleDTO vehicleDTO);
    AppResponseDTO<VehicleDTO> partialUpdateVehicle(VehicleDTO vehicleDTO);
    AppResponseDTO<?> deleteVehicle(UUID id);
}
