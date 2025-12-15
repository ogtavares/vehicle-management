package com.vehicle.management.service.impl;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.VehicleFilterDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.model.entity.Vehicle;
import com.vehicle.management.repository.VehicleManagementRepository;
import com.vehicle.management.service.VehicleManagementService;
import com.vehicle.management.util.JsonMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class VehicleManagementServiceImpl implements VehicleManagementService {
    @Autowired
    VehicleManagementRepository repository;

    @Override
    public AppResponseDTO<Page<VehicleDTO>> getAllVehicles() {
        try {
            List<Vehicle> vehicles = repository.findByActiveTrue();
            if (vehicles.isEmpty()){
                return AppResponseDTO.getSuccessResponse("Não há veículos ativos.");
            }
            return AppResponseDTO.<Page<VehicleDTO>>builder()
                    .content(new PageImpl<>(JsonMapperUtil.convertList(vehicles, VehicleDTO.class)))
                    .status(200)
                    .success(true)
                    .message("Veículos retornados com sucesso!")
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<Page<VehicleDTO>>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar buscar veículos. Por favor, contate o suporte.")
                    .build();
        }
    }

    @Override
    public AppResponseDTO<Page<VehicleDTO>> getVehiclesByFilters(String plate, String brand, Integer year, String color, BigDecimal price) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .plate(plate)
                .brand(brand)
                .year(year)
                .color(color)
                .price(price)
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);
        try {

            List<Vehicle> vehicles = repository.findByFilters(plate, brand, year, color, price);
            if (vehicles.isEmpty()){
                return AppResponseDTO.getSuccessResponse("Não há veículos para os parâmetros informados.", parameters);
            }
            return AppResponseDTO.<Page<VehicleDTO>>builder()
                    .content(new PageImpl<>(JsonMapperUtil.convertList(vehicles, VehicleDTO.class)))
                    .status(200)
                    .success(true)
                    .message("Veículos retornados com sucesso!")
                    .parameters(parameters)
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<Page<VehicleDTO>>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar buscar veículos. Por favor, contate o suporte.")
                    .parameters(parameters)
                    .build();
        }
    }

    @Override
    public AppResponseDTO<Page<VehicleDTO>> getVehiclesBypriceRange(BigDecimal minprice, BigDecimal maxprice) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .minPrice(minprice)
                .maxPrice(maxprice)
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);
        try {
            List<Vehicle> vehicles = repository.findByPriceBetweenAndActiveTrue(minprice, maxprice);
            if (vehicles.isEmpty()){
                return AppResponseDTO.getSuccessResponse("Não há veículos ativos.", parameters);
            }
            return AppResponseDTO.<Page<VehicleDTO>>builder()
                    .content(new PageImpl<>(JsonMapperUtil.convertList(vehicles, VehicleDTO.class)))
                    .status(200)
                    .success(true)
                    .message("Veículos retornados com sucesso!")
                    .parameters(parameters)
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<Page<VehicleDTO>>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar buscar veículos. Por favor, contate o suporte.")
                    .parameters(parameters)
                    .build();
        }
    }

    @Override
    public AppResponseDTO<VehicleDTO> getVehicleById(UUID id) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(id)
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);
        try {
            Optional<Vehicle> vehicle = repository.findByIdAndActiveTrue(id);
            if (vehicle.isEmpty()){
                return AppResponseDTO.getSuccessResponse("Não há veículo ativo para o id informado.", parameters);
            }
            return AppResponseDTO.<VehicleDTO>builder()
                    .content((JsonMapperUtil.convert(vehicle, VehicleDTO.class)))
                    .status(200)
                    .success(true)
                    .message("Veículo retornado com sucesso!")
                    .parameters(parameters)
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<VehicleDTO>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar buscar veículo. Por favor, contate o suporte.")
                    .parameters(parameters)
                    .build();
        }
    }

    @Override
    public AppResponseDTO<List<VehicleBrandReportDTO>> getVehicleBrandReport() {
        try {
            List<Object[]> vehicleCount = repository.countVehiclesByBrand();
            if (vehicleCount.isEmpty()){
                return AppResponseDTO.getSuccessResponse("Não há veículos ativos para criação do relatório.");
            }
            List<VehicleBrandReportDTO> report = vehicleCount.stream()
                    .map(r -> new VehicleBrandReportDTO((String) r[0], (Long) r[1]))
                    .toList();

            return AppResponseDTO.<List<VehicleBrandReportDTO>>builder()
                    .content(report)
                    .status(200)
                    .success(true)
                    .message("Relatório criado com sucesso!")
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<List<VehicleBrandReportDTO>>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar criar relatório. Por favor, contate o suporte.")
                    .build();
        }
    }

    @Override
    public AppResponseDTO<?> addVehicle(VehicleDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .brand(vehicleDTO.getBrand())
                .year(vehicleDTO.getYear())
                .plate(vehicleDTO.getPlate())
                .color(vehicleDTO.getColor())
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);

        try {
            Vehicle newVehicle = repository.save(JsonMapperUtil.convert(vehicleDTO, Vehicle.class));
            return AppResponseDTO.<VehicleDTO>builder()
                    .status(200)
                    .success(true)
                    .content(JsonMapperUtil.convert(newVehicle, VehicleDTO.class))
                    .message("Novo veículo salvo com sucesso!")
                    .parameters(parameters)
                    .build();
        } catch (DataIntegrityViolationException ex) {
            return AppResponseDTO.builder()
                    .status(400)
                    .success(false)
                    .message("Já existe um veículo com esta placa.")
                    .parameters(parameters)
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<VehicleDTO>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar salvar novo veículo. Por favor, contate o suporte.")
                    .parameters(parameters)
                    .build();
        }
    }

    @Override
    public AppResponseDTO<VehicleDTO> updateVehicle(VehicleDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(vehicleDTO.getId())
                .brand(vehicleDTO.getBrand())
                .year(vehicleDTO.getYear())
                .plate(vehicleDTO.getPlate())
                .color(vehicleDTO.getColor())
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);

        try {
            Optional<Vehicle> existing = repository.findByIdAndActiveTrue(vehicleDTO.getId());
            if (existing.isEmpty()) {
                return AppResponseDTO.getSuccessResponse("Veículo não encontrado para atualização.", parameters);
            }

            Vehicle vehicle = existing.get();
            vehicle.setBrand(vehicleDTO.getBrand());
            vehicle.setPlate(vehicleDTO.getPlate());
            vehicle.setColor(vehicleDTO.getColor());
            vehicle.setYear(vehicleDTO.getYear());
            vehicle.setPrice(vehicleDTO.getPrice());

            Vehicle updated = repository.save(vehicle);

            return AppResponseDTO.<VehicleDTO>builder()
                    .status(200)
                    .success(true)
                    .message("Veículo atualizado com sucesso!")
                    .content(JsonMapperUtil.convert(updated, VehicleDTO.class))
                    .parameters(parameters)
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<VehicleDTO>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar atualizar veículo. Por favor, contate o suporte.")
                    .parameters(parameters)
                    .build();
        }
    }

    @Override
    public AppResponseDTO<VehicleDTO> partialUpdateVehicle(VehicleDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(vehicleDTO.getId())
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);

        try {
            Optional<Vehicle> existing = repository.findByIdAndActiveTrue(vehicleDTO.getId());
            if (existing.isEmpty()) {
                return AppResponseDTO.getSuccessResponse("Veículo não encontrado para atualização parcial.", parameters);
            }

            Vehicle vehicle = existing.get();
            if (vehicleDTO.getBrand() != null) vehicle.setBrand(vehicleDTO.getBrand());
            if (vehicleDTO.getPlate() != null) vehicle.setPlate(vehicleDTO.getPlate());
            if (vehicleDTO.getColor() != null) vehicle.setColor(vehicleDTO.getColor());
            if (vehicleDTO.getYear() != null) vehicle.setYear(vehicleDTO.getYear());
            if (vehicleDTO.getPrice() != null) vehicle.setPrice(vehicleDTO.getPrice());

            Vehicle updated = repository.save(vehicle);

            return AppResponseDTO.<VehicleDTO>builder()
                    .status(200)
                    .success(true)
                    .message("Veículo atualizado parcialmente com sucesso!")
                    .content(JsonMapperUtil.convert(updated, VehicleDTO.class))
                    .parameters(parameters)
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<VehicleDTO>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar atualizar parcialmente veículo. Por favor, contate o suporte.")
                    .parameters(parameters)
                    .build();
        }
    }

    @Override
    public AppResponseDTO<?> deleteVehicle(UUID id) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(id)
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);

        try {
            Optional<Vehicle> existing = repository.findByIdAndActiveTrue(id);
            if (existing.isEmpty()) {
                return AppResponseDTO.getSuccessResponse("Veículo não encontrado para exclusão.", parameters);
            }

            repository.deactivateById(id);

            return AppResponseDTO.builder()
                    .status(200)
                    .success(true)
                    .message("Veículo removido com sucesso!")
                    .parameters(parameters)
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar remover veículo. Por favor, contate o suporte.")
                    .parameters(parameters)
                    .build();
        }
    }
}