package com.vehicle.management.service.impl;

import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehiclePatchRequestDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.exception.ConflictException;
import com.vehicle.management.model.entity.Vehicle;
import com.vehicle.management.repository.VehicleManagementRepository;
import com.vehicle.management.service.VehiclePriceConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleManagementServiceImplTest {
    @Mock
    private VehicleManagementRepository repository;

    @Mock
    private VehiclePriceConversionService priceConversionService;

    @InjectMocks
    private VehicleManagementServiceImpl service;

    private Vehicle vehicle;
    private VehicleRequestDTO requestDTO;

    @BeforeEach
    void setup() {
        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setPlate("ABC1234");
        vehicle.setBrand("Toyota");
        vehicle.setColor("Preto");
        vehicle.setVehicleYear(2022);
        vehicle.setPrice(new BigDecimal("20000"));
        vehicle.setActive(true);

        requestDTO = VehicleRequestDTO.builder()
                .plate("ABC1234")
                .brand("Toyota")
                .color("Preto")
                .vehicleYear(2022)
                .price(new BigDecimal("100000"))
                .build();
    }

    @Test
    void shouldReturnVehiclesWhenFiltersMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));

        when(priceConversionService.convertBrlToUsd(any()))
                .thenReturn(new BigDecimal("20000"));
        when(priceConversionService.convertUsdToBrl(any()))
                .thenReturn(new BigDecimal("100000"));
        when(repository.findByFilters(
                any(), any(), any(), any(), any(), any(), eq(pageable))
        ).thenReturn(page);

        AppResponseDTO<Page<VehicleDTO>> response =
                service.getVehiclesByFilters(
                        null, "Toyota", 2022, "Preto",
                        new BigDecimal("90000"), null, pageable
                );

        assertTrue(response.getSuccess());
        assertEquals(1, response.getContent().getTotalElements());
        verify(repository).findByFilters(any(), any(), any(), any(), any(), any(), eq(pageable));
    }

    @Test
    void shouldReturnEmptyMessageWhenNoVehiclesFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByFilters(any(), any(), any(), any(), any(), any(), eq(pageable)))
                .thenReturn(Page.empty());

        AppResponseDTO<Page<VehicleDTO>> response =
                service.getVehiclesByFilters(null, null, null, null, null, null, pageable);

        assertTrue(response.getSuccess());
        assertNull(response.getContent());
        assertEquals("Não há veículos para os parâmetros informados.", response.getMessage());
    }

    @Test
    void shouldFailWhenPlateAlreadyExists() {
        when(repository.findByPlateAndActiveTrue("ABC1234"))
                .thenReturn(Optional.of(vehicle));

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> service.addVehicle(requestDTO)
        );

        assertTrue(ex.getMessage().contains("Já existe um veículo"));
    }

    @Test
    void shouldSaveVehicleSuccessfully() {
        when(repository.findByPlateAndActiveTrue("ABC1234"))
                .thenReturn(Optional.empty());
        when(priceConversionService.convertBrlToUsd(any()))
                .thenReturn(new BigDecimal("20000"));
        when(priceConversionService.convertUsdToBrl(any()))
                .thenReturn(new BigDecimal("100000"));
        when(repository.save(any(Vehicle.class)))
                .thenReturn(vehicle);

        AppResponseDTO<VehicleDTO> response = service.addVehicle(requestDTO);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getContent());
        verify(repository).save(any(Vehicle.class));
    }


    @Test
    void shouldFailUpdateWhenVehicleNotFound() {
        when(repository.findByIdAndActiveTrue(any()))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateVehicle(UUID.randomUUID(), requestDTO)
        );
    }

    @Test
    void shouldUpdateVehicleSuccessfully() {
        when(repository.findByIdAndActiveTrue(any()))
                .thenReturn(Optional.of(vehicle));
        when(priceConversionService.convertBrlToUsd(any()))
                .thenReturn(new BigDecimal("20000"));
        when(priceConversionService.convertUsdToBrl(any()))
                .thenReturn(new BigDecimal("100000"));
        when(repository.save(any()))
                .thenReturn(vehicle);

        AppResponseDTO<VehicleDTO> response =
                service.updateVehicle(vehicle.getId(), requestDTO);

        assertTrue(response.getSuccess());
        verify(repository).save(vehicle);
    }


    @Test
    void shouldApplyPartialUpdate() {
        VehiclePatchRequestDTO patchDTO = VehiclePatchRequestDTO.builder()
                .color("Azul")
                .build();

        when(repository.findByIdAndActiveTrue(any()))
                .thenReturn(Optional.of(vehicle));
        when(repository.save(any()))
                .thenReturn(vehicle);

        AppResponseDTO<VehicleDTO> response =
                service.partialUpdateVehicle(vehicle.getId(), patchDTO);

        assertTrue(response.getSuccess());
        assertEquals("Azul", response.getContent().getColor());
    }

    @Test
    void shouldDeactivateVehicle() {
        when(repository.findByIdAndActiveTrue(any()))
                .thenReturn(Optional.of(vehicle));

        AppResponseDTO<?> response =
                service.deleteVehicle(vehicle.getId());

        assertTrue(response.getSuccess());
        verify(repository).deactivateById(vehicle.getId());
    }
}