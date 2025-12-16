package com.vehicle.management.controller;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.dto.response.AppResponse;
import com.vehicle.management.service.VehicleManagementService;
import com.vehicle.management.util.VehicleSortMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<AppResponse<Page<VehicleDTO>>> getVehicles(
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

        AppResponse<Page<VehicleDTO>> response = vehicleManagementService.getVehiclesByFilters(plate, brand, year, color, minPrice, maxPrice, mappedPageable);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<VehicleDTO>> getVehicleById(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id) {
        AppResponse<VehicleDTO> response = vehicleManagementService.getVehicleById(id);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppResponse<?>> addVehicle(@RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        AppResponse<?> response = vehicleManagementService.addVehicle(vehicleDTO);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<VehicleDTO>> updateVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id, @RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        AppResponse<VehicleDTO> response =vehicleManagementService.updateVehicle(id, vehicleDTO);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppResponse<VehicleDTO>> partialUpdateVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id, @RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        AppResponse<VehicleDTO> response = vehicleManagementService.partialUpdateVehicle(id, vehicleDTO);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse<?>> deleteVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id) {
        AppResponse<?> response = vehicleManagementService.deleteVehicle(id);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping("/relatorios/por-marca")
    public ResponseEntity<AppResponse<Page<VehicleBrandReportDTO>>> getVehicleBrandReport(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        AppResponse<Page<VehicleBrandReportDTO>> response = vehicleManagementService.getVehicleBrandReport(pageable);

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