package com.vehicle.management.service.impl;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.VehicleFilterDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.model.entity.Vehicle;
import com.vehicle.management.repository.VehicleManagementRepository;
import com.vehicle.management.service.VehicleManagementService;
import com.vehicle.management.service.VehiclePriceConversionService;
import com.vehicle.management.util.JsonMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    public AppResponseDTO<Page<VehicleDTO>> getAllVehicles(Pageable pageable) {
        try {
            Page<Vehicle> vehicles = repository.findByActiveTrue(pageable);

            if (!vehicles.hasContent()){
                return AppResponseDTO.getSuccessResponse("Não há veículos ativos.");
            }

            List<VehicleDTO> dtoList = JsonMapperUtil.convertList(vehicles.getContent(), VehicleDTO.class);
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
    public AppResponseDTO<Page<VehicleDTO>> getVehiclesByFilters(String plate, String brand, Integer year, String color, BigDecimal price, Pageable pageable) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .plate(plate)
                .brand(brand)
                .year(year)
                .color(color)
                .price(price)
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);
        try {

            Page<Vehicle> vehicles = repository.findByFilters(plate, brand, year, color, price, pageable);
            if (!vehicles.hasContent()){
                return AppResponseDTO.getSuccessResponse("Não há veículos para os parâmetros informados.", parameters);
            }
            List<VehicleDTO> dtoList = JsonMapperUtil.convertList(vehicles.getContent(), VehicleDTO.class);
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
    public AppResponseDTO<Page<VehicleDTO>> getVehiclesBypriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);

        if (nonNull(minPrice) && nonNull(maxPrice) && minPrice.compareTo(maxPrice) > 0) {
            return AppResponseDTO.<Page<VehicleDTO>>builder()
                    .status(400)
                    .success(false)
                    .message("O preço mínimo não pode ser maior que o preço máximo.")
                    .parameters(parameters)
                    .build();
        }
        try {
            Page<Vehicle> vehicles = repository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice, pageable);
            if (!vehicles.hasContent()){
                return AppResponseDTO.getSuccessResponse("Não há veículos ativos.", parameters);
            }
            List<VehicleDTO> dtoList = JsonMapperUtil.convertList(vehicles.getContent(), VehicleDTO.class);
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
            VehicleDTO dto = JsonMapperUtil.convert(vehicle, VehicleDTO.class);
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
    public AppResponseDTO<Page<VehicleBrandReportDTO>> getVehicleBrandReport(Pageable pageable) {
        try {
            Page<Object[]> vehicleCountPage = repository.countVehiclesByBrand(pageable);

            if (!vehicleCountPage.hasContent()) {
                return AppResponseDTO.getSuccessResponse(
                        "Não há veículos ativos para criação do relatório."
                );
            }

            List<VehicleBrandReportDTO> report = vehicleCountPage.getContent().stream()
                    .map(r -> new VehicleBrandReportDTO((String) r[0], (Long) r[1]))
                    .toList();

            Page<VehicleBrandReportDTO> pageReport = new PageImpl<>(report, pageable, vehicleCountPage.getTotalElements());

            return AppResponseDTO.<Page<VehicleBrandReportDTO>>builder()
                    .content(pageReport)
                    .status(200)
                    .success(true)
                    .message("Relatório criado com sucesso!")
                    .build();
        } catch (Exception e) {
            return AppResponseDTO.<Page<VehicleBrandReportDTO>>builder()
                    .status(500)
                    .success(false)
                    .message("Erro ao tentar criar relatório. Por favor, contate o suporte.")
                    .build();
        }
    }


    @Override
    public AppResponseDTO<?> addVehicle(VehicleRequestDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .brand(vehicleDTO.getBrand())
                .year(vehicleDTO.getYear())
                .plate(vehicleDTO.getPlate())
                .color(vehicleDTO.getColor())
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);

        try {
            Vehicle vehicle = JsonMapperUtil.convert(vehicleDTO, Vehicle.class);
            if (nonNull(vehicleDTO.getPrice())) {
                vehicle.setPrice(vehiclePriceConversionService.convertBrlToUsd(vehicleDTO.getPrice()));
            }
            Vehicle savedVehicle = repository.save(vehicle);

            VehicleDTO savedDTO = JsonMapperUtil.convert(savedVehicle, VehicleDTO.class);

            if (nonNull(savedDTO.getPrice())){
                savedDTO.setPrice(vehiclePriceConversionService.convertUsdToBrl(savedDTO.getPrice()));
            }
            return AppResponseDTO.<VehicleDTO>builder()
                    .status(200)
                    .success(true)
                    .content(savedDTO)
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
    public AppResponseDTO<VehicleDTO> updateVehicle(UUID id, VehicleRequestDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(id)
                .brand(vehicleDTO.getBrand())
                .year(vehicleDTO.getYear())
                .plate(vehicleDTO.getPlate())
                .color(vehicleDTO.getColor())
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);

        try {
            Optional<Vehicle> existing = repository.findByIdAndActiveTrue(id);
            if (existing.isEmpty()) {
                return AppResponseDTO.getSuccessResponse("Veículo não encontrado para atualização.", parameters);
            }

            Vehicle vehicle = existing.get();
            vehicle.setBrand(vehicleDTO.getBrand());
            vehicle.setPlate(vehicleDTO.getPlate());
            vehicle.setColor(vehicleDTO.getColor());
            vehicle.setYear(vehicleDTO.getYear());
            if (vehicleDTO.getPrice() != null) {
                vehicle.setPrice(vehiclePriceConversionService.convertBrlToUsd(vehicleDTO.getPrice()));
            }

            Vehicle updated = repository.save(vehicle);

            VehicleDTO updatedDto = JsonMapperUtil.convert(updated, VehicleDTO.class);
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
    public AppResponseDTO<VehicleDTO> partialUpdateVehicle(UUID id, VehicleRequestDTO vehicleDTO) {
        VehicleFilterDTO filters = VehicleFilterDTO.builder()
                .id(id)
                .build();
        Map<String, Object> parameters = JsonMapperUtil.toNonNullMap(filters);

        try {
            Optional<Vehicle> existing = repository.findByIdAndActiveTrue(id);
            if (existing.isEmpty()) {
                return AppResponseDTO.getSuccessResponse("Veículo não encontrado para atualização parcial.", parameters);
            }

            Vehicle vehicle = existing.get();

            applyPartialUpdates(vehicle, vehicleDTO);

            if (nonNull(vehicleDTO.getPrice())) {
                vehicle.setPrice(vehiclePriceConversionService.convertBrlToUsd(vehicleDTO.getPrice()));
            }

            Vehicle updated = repository.save(vehicle);

            VehicleDTO updatedDto = JsonMapperUtil.convert(updated, VehicleDTO.class);
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

    private void applyPartialUpdates(Vehicle vehicle, VehicleRequestDTO vehicleDTO) {
        if (nonNull(vehicleDTO.getBrand())) vehicle.setBrand(vehicleDTO.getBrand());
        if (nonNull(vehicleDTO.getPlate())) vehicle.setPlate(vehicleDTO.getPlate());
        if (nonNull(vehicleDTO.getColor())) vehicle.setColor(vehicleDTO.getColor());
        if (nonNull(vehicleDTO.getYear())) vehicle.setYear(vehicleDTO.getYear());
    }
}