package com.vehicle.management.service;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;

import com.vehicle.management.dto.response.AppResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface VehicleManagementService {
    AppResponse<Page<VehicleDTO>> getVehiclesByFilters(String plate, String brand, Integer vehicleYear, String color, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    AppResponse<VehicleDTO> getVehicleById(UUID id);
    AppResponse<Page<VehicleBrandReportDTO>> getVehicleBrandReport(Pageable pageable);
    AppResponse<VehicleDTO> addVehicle(VehicleRequestDTO vehicleDTO);
    AppResponse<VehicleDTO> updateVehicle(UUID id, VehicleRequestDTO vehicleDTO);
    AppResponse<VehicleDTO> partialUpdateVehicle(UUID id, VehicleRequestDTO vehicleDTO);
    AppResponse<?> deleteVehicle(UUID id);
}
