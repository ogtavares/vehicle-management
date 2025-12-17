package com.vehicle.management.mapper;

import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.model.entity.Vehicle;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class VehicleMapper {
    public static Vehicle toEntity(VehicleRequestDTO dto) {
        if (dto == null) return null;

        Vehicle vehicle = new Vehicle();
        vehicle.setPlate(dto.getPlate());
        vehicle.setBrand(dto.getBrand());
        vehicle.setColor(dto.getColor());
        vehicle.setVehicleYear(dto.getVehicleYear());
        vehicle.setPrice(dto.getPrice());
        return vehicle;
    }

    public static VehicleDTO toDTO(Vehicle entity) {
        if (entity == null) return null;

        VehicleDTO dto = new VehicleDTO();
        dto.setId(entity.getId());
        dto.setPlate(entity.getPlate());
        dto.setBrand(entity.getBrand());
        dto.setColor(entity.getColor());
        dto.setVehicleYear(entity.getVehicleYear());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    public static List<VehicleDTO> toDTOList(List<Vehicle> entities) {
        return entities.stream()
                .map(VehicleMapper::toDTO)
                .toList();
    }
}