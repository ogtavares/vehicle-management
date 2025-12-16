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
    public AppResponse<Page<VehicleDTO>> getVehicles(
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
        return vehicleManagementService.getVehiclesByFilters(
                plate, brand, year, color, minPrice, maxPrice, mappedPageable
        );
    }

    @GetMapping("/{id}")
    public AppResponse<VehicleDTO> getVehicleById(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id) {
        return vehicleManagementService.getVehicleById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponse<?> addVehicle(@RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        return vehicleManagementService.addVehicle(vehicleDTO);
    }

    @PutMapping("/{id}")
    public AppResponse<VehicleDTO> updateVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id, @RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        return vehicleManagementService.updateVehicle(id, vehicleDTO);
    }

    @PatchMapping("/{id}")
    public AppResponse<VehicleDTO> partialUpdateVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id, @RequestBody @Valid VehicleRequestDTO vehicleDTO) {
        return vehicleManagementService.partialUpdateVehicle(id, vehicleDTO);
    }

    @DeleteMapping("/{id}")
    public AppResponse<?> deleteVehicle(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "ID inválido") UUID id) {
        return vehicleManagementService.deleteVehicle(id);
    }

    @GetMapping("/relatorios/por-marca")
    public AppResponse<Page<VehicleBrandReportDTO>> getVehicleBrandReport(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return vehicleManagementService.getVehicleBrandReport(pageable);
    }

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