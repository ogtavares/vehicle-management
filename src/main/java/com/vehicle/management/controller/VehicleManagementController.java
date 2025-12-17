package com.vehicle.management.controller;

import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehiclePatchRequestDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.service.VehicleManagementService;
import com.vehicle.management.mapper.VehicleSortMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Veículos", description = "Operações relacionadas ao gerenciamento de veículos.")
@SecurityRequirement(name = "bearerAuth")
public class VehicleManagementController {

    private final Logger logger = LoggerFactory.getLogger(VehicleManagementController.class);

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @Operation(
            summary = "Listar veículos",
            description = "Lista veículos com filtros opcionais e paginação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículos retornados com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão")
            }
    )
    @GetMapping
    public ResponseEntity<AppResponseDTO<Page<VehicleDTO>>> getVehicles(
            @Parameter(description = "Placa do veículo", example = "ABC1234")
            @RequestParam(name = "placa", required = false) String plate,

            @Parameter(description = "Marca do veículo", example = "Toyota")
            @RequestParam(name = "marca", required = false) String brand,

            @Parameter(description = "Ano do veículo", example = "2022")
            @RequestParam(name = "ano", required = false) Integer year,

            @Parameter(description = "Cor do veículo", example = "Preto")
            @RequestParam(name = "cor", required = false)
            @Pattern(
                    regexp = "^[A-Za-zÀ-ÿ\\s]+$",
                    message = "O parâmetro 'cor' deve conter apenas letras"
            )
            String color,

            @Parameter(description = "Preço mínimo", example = "30000")
            @RequestParam(name = "minPreco", required = false) BigDecimal minPrice,

            @Parameter(description = "Preço máximo", example = "100000")
            @RequestParam(name = "maxPreco", required = false) BigDecimal maxPrice,

            @Parameter(hidden = true)
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Pageable mappedPageable = mapPageable(pageable);

        AppResponseDTO<Page<VehicleDTO>> response =
                vehicleManagementService.getVehiclesByFilters(
                        plate, brand, year, color, minPrice, maxPrice, mappedPageable
                );

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Buscar veículo por ID",
            description = "Retorna os detalhes de um veículo específico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
                    @ApiResponse(responseCode = "400", description = "ID inválido"),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppResponseDTO<VehicleDTO>> getVehicleById(
            @Parameter(
                    description = "ID do veículo",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable
            @NotBlank
            @Pattern(
                    regexp = "^[0-9a-fA-F\\-]{36}$",
                    message = "ID inválido"
            )
            String id
    ) {

        AppResponseDTO<VehicleDTO> response =
                vehicleManagementService.getVehicleById(UUID.fromString(id));

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Criar veículo",
            description = "Cria um novo veículo (somente ADMIN)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro de validação"),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão"),
                    @ApiResponse(responseCode = "409", description = "Placa já existente")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppResponseDTO<VehicleDTO>> addVehicle(
            @RequestBody @Valid VehicleRequestDTO vehicleDTO
    ) {

        AppResponseDTO<VehicleDTO> response =
                vehicleManagementService.addVehicle(vehicleDTO);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Atualização completa de veículo",
            description = "Atualiza todos os dados de um veículo existente. Acesso restrito a usuários ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou erro de validação"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão para atualizar veículos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito de dados: já existe outro veículo com a placa informada"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AppResponseDTO<VehicleDTO>> updateVehicle(
            @PathVariable String id,
            @RequestBody @Valid VehicleRequestDTO vehicleDTO
    ) {

        AppResponseDTO<VehicleDTO> response =
                vehicleManagementService.updateVehicle(UUID.fromString(id), vehicleDTO);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Atualização parcial de veículo",
            description = "Atualiza parcialmente os dados de um veículo existente. Acesso restrito a usuários ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo atualizado parcialmente com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou erro de validação"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão para atualizar veículos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito de dados: já existe outro veículo com a placa informada"
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AppResponseDTO<VehicleDTO>> partialUpdateVehicle(
            @PathVariable String id,
            @RequestBody VehiclePatchRequestDTO vehicleDTO
    ) {

        AppResponseDTO<VehicleDTO> response =
                vehicleManagementService.partialUpdateVehicle(UUID.fromString(id), vehicleDTO);

        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @Operation(
            summary = "Remover veículo",
            description = "Remove (desativa) um veículo (somente ADMIN)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponseDTO<?>> deleteVehicle(@PathVariable String id) {

        AppResponseDTO<?> response =
                vehicleManagementService.deleteVehicle(UUID.fromString(id));

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Relatório de veículos por marca",
            description = "Retorna um relatório com a quantidade de veículos por marca"
    )
    @GetMapping("/relatorios/por-marca")
    public ResponseEntity<AppResponseDTO<Page<VehicleBrandReportDTO>>> getVehicleBrandReport(
            @Parameter(hidden = true)
            @PageableDefault(sort = "brand", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        AppResponseDTO<Page<VehicleBrandReportDTO>> response =
                vehicleManagementService.getVehicleBrandReport(pageable);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private Pageable mapPageable(Pageable pageable) {

        Sort mappedSort = pageable.getSort().stream()
                .map(order -> {
                    String mappedProperty = VehicleSortMapper.SORT_FIELDS
                            .getOrDefault(order.getProperty(), order.getProperty());
                    return new Sort.Order(order.getDirection(), mappedProperty);
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), Sort::by));

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                mappedSort
        );
    }
}