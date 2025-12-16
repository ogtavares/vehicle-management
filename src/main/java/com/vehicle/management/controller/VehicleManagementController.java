package com.vehicle.management.controller;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehiclePatchRequestDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.service.VehicleManagementService;
import com.vehicle.management.mapper.VehicleSortMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/veiculos")
@Validated
public class VehicleManagementController {

    private final Logger logger = LoggerFactory.getLogger(VehicleManagementController.class);

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @GetMapping
    public ResponseEntity<AppResponseDTO<Page<VehicleDTO>>> getVehicles(
            @RequestParam(name = "placa", required = false) String plate,
            @RequestParam(name = "marca", required = false) String brand,
            @RequestParam(name = "ano", required = false) Integer year,
            @RequestParam(name = "cor", required = false)
            @Pattern(
                    regexp = "^[A-Za-zÀ-ÿ\\s]+$",
                    message = "O parâmetro 'cor' deve conter apenas letras"
            )
            String color,
            @RequestParam(name = "minPreco", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPreco", required = false) BigDecimal maxPrice,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        logger.info("Iniciando busca de veículos");
        logger.info("Filtros -- placa: {}, marca: {}, ano: {}, cor: {}, preço mínimo: {}, preço máximo: {}", plate, brand, year, color, minPrice, maxPrice);

        Pageable mappedPageable = mapPageable(pageable);

        AppResponseDTO<Page<VehicleDTO>> response = vehicleManagementService.getVehiclesByFilters(plate, brand, year, color, minPrice, maxPrice, mappedPageable);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppResponseDTO<VehicleDTO>> getVehicleById(
            @PathVariable
            @NotBlank
            @Pattern(
                    regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "ID inválido"
            )
            String id
    ) {
        logger.info("Id antes da conversão: {}", id);
        UUID uuid = UUID.fromString(id);
        logger.info("Id depois da conversão: {}", uuid);
        AppResponseDTO<VehicleDTO> response = vehicleManagementService.getVehicleById(uuid);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppResponseDTO<VehicleDTO>> addVehicle(@RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        AppResponseDTO<VehicleDTO> response = vehicleManagementService.addVehicle(vehicleDTO);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppResponseDTO<VehicleDTO>> updateVehicle(@PathVariable @NotBlank @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "ID inválido") String id, @RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        AppResponseDTO<VehicleDTO> response = vehicleManagementService.updateVehicle(UUID.fromString(id), vehicleDTO);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppResponseDTO<VehicleDTO>> partialUpdateVehicle(@PathVariable @NotBlank @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "ID inválido") String id, @RequestBody VehiclePatchRequestDTO vehicleDTO) {
        AppResponseDTO<VehicleDTO> response = vehicleManagementService.partialUpdateVehicle(UUID.fromString(id), vehicleDTO);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponseDTO<?>> deleteVehicle(@PathVariable @NotBlank @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "ID inválido") String id) {
        AppResponseDTO<?> response = vehicleManagementService.deleteVehicle(UUID.fromString(id));

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping("/relatorios/por-marca")
    public ResponseEntity<AppResponseDTO<Page<VehicleBrandReportDTO>>> getVehicleBrandReport(@PageableDefault(sort = "brand", direction = Sort.Direction.ASC) Pageable pageable) {
        AppResponseDTO<Page<VehicleBrandReportDTO>> response = vehicleManagementService.getVehicleBrandReport(pageable);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);    }

    private Pageable mapPageable(Pageable pageable) {

        Sort mappedSort = pageable.getSort().stream()
                .map(order -> {
                    String mappedProperty = VehicleSortMapper.SORT_FIELDS
                            .getOrDefault(order.getProperty(), order.getProperty());

                    return new Sort.Order(order.getDirection(), mappedProperty);
                })
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        Sort::by
                ));

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                mappedSort
        );
    }
}