package com.vehicle.management.service.impl;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.VehicleFilterDTO;
import com.vehicle.management.dto.request.VehiclePatchRequestDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.mapper.VehicleMapper;
import com.vehicle.management.model.entity.Vehicle;
import com.vehicle.management.repository.VehicleManagementRepository;
import com.vehicle.management.service.VehicleManagementService;
import com.vehicle.management.service.VehiclePriceConversionService;
import com.vehicle.management.mapper.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Objects.nonNull;

@Service
public class VehicleManagementServiceImpl implements VehicleManagementService {
    @Autowired
    VehicleManagementRepository repository;

    @Autowired
    VehiclePriceConversionService vehiclePriceConversionService;

    @Override
    public AppResponseDTO<Page<VehicleDTO>> getVehiclesByFilters(
            String plate,
            String brand,
            Integer vehicleYear,
            String color,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {

        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .plate(plate)
                .brand(brand)
                .vehicleYear(vehicleYear)
                .color(color)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Map<String, Object> parameters = JsonMapper.toNonNullMap(filters);

        if (nonNull(minPrice)) {
            minPrice = vehiclePriceConversionService.convertBrlToUsd(minPrice);
        }
        if (nonNull(maxPrice)) {
            maxPrice = vehiclePriceConversionService.convertBrlToUsd(maxPrice);
        }

        Page<Vehicle> vehicles = repository.findByFilters(
                plate, brand, vehicleYear, color, minPrice, maxPrice, pageable
        );

        if (!vehicles.hasContent()) {
            return AppResponseDTO.getSuccessResponse(
                    "Não há veículos para os parâmetros informados.",
                    parameters
            );
        }

        List<VehicleDTO> dtoList = VehicleMapper.toDTOList(vehicles.getContent());

        dtoList.forEach(v -> {
            if (nonNull(v.getPrice())) {
                v.setPrice(vehiclePriceConversionService.convertUsdToBrl(v.getPrice()));
            }
        });

        return AppResponseDTO.<Page<VehicleDTO>>builder()
                .content(new PageImpl<>(dtoList, vehicles.getPageable(), vehicles.getTotalElements()))
                .status(200)
                .success(true)
                .message("Veículos retornados com sucesso!")
                .parameters(parameters)
                .build();
    }


    @Override
    public AppResponseDTO<VehicleDTO> getVehicleById(UUID id) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(id)
                .build();
        Map<String, Object> parameters = JsonMapper.toNonNullMap(filters);
            Optional<Vehicle> vehicle = repository.findByIdAndActiveTrue(id);
            if (vehicle.isEmpty()){
                return AppResponseDTO.getSuccessResponse("Não há veículo ativo para o id informado.", parameters);
            }
        VehicleDTO dto = VehicleMapper.toDTO(vehicle.get());
            if (nonNull(dto.getPrice())) {
                dto.setPrice(vehiclePriceConversionService.convertUsdToBrl(dto.getPrice()));
            }

            return AppResponseDTO.<VehicleDTO>builder()
                    .content(dto)
                    .status(200)
                    .success(true)
                    .message("Veículo retornado com sucesso!")
                    .parameters(parameters)
                    .build();
    }

    @Override
    public AppResponseDTO<Page<VehicleBrandReportDTO>> getVehicleBrandReport(Pageable pageable) {
        Page<VehicleBrandReportDTO> vehicleCountPage = repository.countVehiclesByBrand(pageable);
        if (!vehicleCountPage.hasContent()) {
            return AppResponseDTO.getSuccessResponse(
                    "Não há veículos ativos para a criação do relatório."
            );
        }

        return AppResponseDTO.<Page<VehicleBrandReportDTO>>builder()
                .content(vehicleCountPage)
                .status(200)
                .success(true)
                .message("Relatório criado com sucesso!")
                .build();
    }

    @Override
    public AppResponseDTO<VehicleDTO> addVehicle(VehicleRequestDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .brand(vehicleDTO.getBrand())
                .vehicleYear(vehicleDTO.getVehicleYear())
                .plate(vehicleDTO.getPlate())
                .color(vehicleDTO.getColor())
                .price(vehicleDTO.getPrice())
                .build();
        Map<String, Object> parameters = JsonMapper.toNonNullMap(filters);

        Optional<Vehicle> existentVehicle = repository.findByPlateAndActiveTrue(vehicleDTO.getPlate());
        if (existentVehicle.isPresent()) {
            throw new IllegalArgumentException(String.format("Já existe um veículo com a placa (%s) informada.", vehicleDTO.getPlate()));
        }

        Vehicle vehicle = VehicleMapper.toEntity(vehicleDTO);
        if (nonNull(vehicleDTO.getPrice())) {
            vehicle.setPrice(vehiclePriceConversionService.convertBrlToUsd(vehicleDTO.getPrice()));
        }
        Vehicle savedVehicle = repository.save(vehicle);

        VehicleDTO savedDTO = VehicleMapper.toDTO(savedVehicle);

        if (nonNull(savedDTO.getPrice())){
            savedDTO.setPrice(vehiclePriceConversionService.convertUsdToBrl(savedDTO.getPrice()));
        }
        return AppResponseDTO.<VehicleDTO>builder()
                .status(201)
                .success(true)
                .content(savedDTO)
                .message("Novo veículo salvo com sucesso!")
                .parameters(parameters)
                .build();
    }

    @Override
    public AppResponseDTO<VehicleDTO> updateVehicle(UUID id, VehicleRequestDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(id)
                .brand(vehicleDTO.getBrand())
                .vehicleYear(vehicleDTO.getVehicleYear())
                .plate(vehicleDTO.getPlate())
                .color(vehicleDTO.getColor())
                .price(vehicleDTO.getPrice())
                .build();
        Map<String, Object> parameters = JsonMapper.toNonNullMap(filters);

        Optional<Vehicle> existing = repository.findByIdAndActiveTrue(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException(String.format("Veículo com o id (%s) informado não encontrado para atualização.", id));
        }

        Vehicle vehicle = existing.get();
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setPlate(vehicleDTO.getPlate());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setVehicleYear(vehicleDTO.getVehicleYear());
        if (vehicleDTO.getPrice() != null) {
            vehicle.setPrice(vehiclePriceConversionService.convertBrlToUsd(vehicleDTO.getPrice()));
        }

        Vehicle updated = repository.save(vehicle);

        VehicleDTO updatedDto = VehicleMapper.toDTO(updated);
        if (nonNull(updatedDto.getPrice())) {
            updatedDto.setPrice(vehiclePriceConversionService.convertUsdToBrl(updatedDto.getPrice()));
        }

        return AppResponseDTO.<VehicleDTO>builder()
                .status(200)
                .success(true)
                .message("Veículo atualizado com sucesso!")
                .content(updatedDto)
                .parameters(parameters)
                .build();
    }

    @Override
    public AppResponseDTO<VehicleDTO> partialUpdateVehicle(UUID id, VehiclePatchRequestDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(id)
                .build();
        Map<String, Object> parameters = JsonMapper.toNonNullMap(filters);

        Optional<Vehicle> existing = repository.findByIdAndActiveTrue(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException(String.format("Veículo com o id (%s) informado não encontrado para atualização parcial", id));
        }

        Vehicle vehicle = existing.get();

        applyPartialUpdates(vehicle, vehicleDTO);

        if (nonNull(vehicleDTO.getPrice())) {
            vehicle.setPrice(vehiclePriceConversionService.convertBrlToUsd(vehicleDTO.getPrice()));
        }

        Vehicle updated = repository.save(vehicle);

        VehicleDTO updatedDto = VehicleMapper.toDTO(updated);
        if (nonNull(updatedDto.getPrice())) {
            updatedDto.setPrice(vehiclePriceConversionService.convertUsdToBrl(updatedDto.getPrice()));
        }

        return AppResponseDTO.<VehicleDTO>builder()
                .status(200)
                .success(true)
                .message("Veículo atualizado parcialmente com sucesso!")
                .content(updatedDto)
                .parameters(parameters)
                .build();
    }

    @Override
    public AppResponseDTO<?> deleteVehicle(UUID id) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(id)
                .build();
        Map<String, Object> parameters = JsonMapper.toNonNullMap(filters);

        Optional<Vehicle> existing = repository.findByIdAndActiveTrue(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException(String.format("Veículo com o id (%s) informado não encontrado para exclusão.", id));
        }

        repository.deactivateById(id);

        return AppResponseDTO.builder()
                .status(200)
                .success(true)
                .message("Veículo removido com sucesso!")
                .parameters(parameters)
                .build();
    }

    private void applyPartialUpdates(Vehicle vehicle, VehiclePatchRequestDTO vehicleDTO) {
        if (nonNull(vehicleDTO.getBrand())) vehicle.setBrand(vehicleDTO.getBrand());
        if (nonNull(vehicleDTO.getPlate())) vehicle.setPlate(vehicleDTO.getPlate());
        if (nonNull(vehicleDTO.getColor())) vehicle.setColor(vehicleDTO.getColor());
        if (nonNull(vehicleDTO.getVehicleYear())) vehicle.setVehicleYear(vehicleDTO.getVehicleYear());
    }
}