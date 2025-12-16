package com.vehicle.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.management.dto.VehicleBrandReportDTO;
import com.vehicle.management.dto.VehicleDTO;
import com.vehicle.management.dto.request.VehiclePatchRequestDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import com.vehicle.management.dto.response.AppResponseDTO;
import com.vehicle.management.security.jwt.service.JwtService;
import com.vehicle.management.service.VehicleManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleManagementService vehicleManagementService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldGetVehiclesSuccessfully() throws Exception {
        VehicleDTO vehicle = VehicleDTO.builder()
                .id(UUID.randomUUID())
                .brand("Toyota")
                .plate("ABC1234")
                .build();

        Page<VehicleDTO> page = new PageImpl<>(List.of(vehicle));

        when(vehicleManagementService.getVehiclesByFilters(
                any(), any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(
                AppResponseDTO.<Page<VehicleDTO>>builder()
                        .status(200)
                        .success(true)
                        .content(page)
                        .build()
        );

        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.content[0].marca").value("Toyota"));
    }

    @Test
    void shouldReturn400WhenColorIsInvalid() throws Exception {
        mockMvc.perform(get("/veiculos")
                        .param("cor", "Azul123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetVehicleById() throws Exception {
        UUID id = UUID.randomUUID();

        VehicleDTO dto = VehicleDTO.builder()
                .id(id)
                .brand("Honda")
                .build();

        when(vehicleManagementService.getVehicleById(id))
                .thenReturn(AppResponseDTO.<VehicleDTO>builder()
                        .status(200)
                        .success(true)
                        .content(dto)
                        .build());

        mockMvc.perform(get("/veiculos/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.marca").value("Honda"));
    }

    @Test
    void shouldReturn400WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/veiculos/123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateVehicle() throws Exception {
        VehicleRequestDTO request = VehicleRequestDTO.builder()
                .brand("Ford")
                .plate("DEF1234")
                .color("Preto")
                .vehicleYear(2022)
                .price(BigDecimal.valueOf(50000))
                .build();

        VehicleDTO responseDto = VehicleDTO.builder()
                .id(UUID.randomUUID())
                .brand("Ford")
                .build();

        when(vehicleManagementService.addVehicle(any()))
                .thenReturn(AppResponseDTO.<VehicleDTO>builder()
                        .status(201)
                        .success(true)
                        .content(responseDto)
                        .build());

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldUpdateVehicle() throws Exception {
        UUID id = UUID.randomUUID();

        VehicleRequestDTO request = VehicleRequestDTO.builder()
                .brand("BMW")
                .plate("XYZ9999")
                .color("Branco")
                .vehicleYear(2020)
                .price(BigDecimal.valueOf(90000))
                .build();

        when(vehicleManagementService.updateVehicle(eq(id), any()))
                .thenReturn(AppResponseDTO.<VehicleDTO>builder()
                        .status(200)
                        .success(true)
                        .build());

        mockMvc.perform(put("/veiculos/{id}", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPartialUpdateVehicle() throws Exception {
        UUID id = UUID.randomUUID();

        VehiclePatchRequestDTO patch = VehiclePatchRequestDTO.builder()
                .color("Azul")
                .build();

        when(vehicleManagementService.partialUpdateVehicle(eq(id), any()))
                .thenReturn(AppResponseDTO.<VehicleDTO>builder()
                        .status(200)
                        .success(true)
                        .build());

        mockMvc.perform(patch("/veiculos/{id}", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteVehicle() throws Exception {
        UUID id = UUID.randomUUID();

        when(vehicleManagementService.deleteVehicle(id))
                .thenReturn(AppResponseDTO.getSuccessResponse("Veículo deletado com sucesso!"));

        mockMvc.perform(delete("/veiculos/{id}", id.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetVehicleBrandReport() throws Exception {
        Page<VehicleBrandReportDTO> page = new PageImpl<>(
                List.of(new VehicleBrandReportDTO("Toyota", 2L))
        );

        when(vehicleManagementService.getVehicleBrandReport(any()))
                .thenReturn(AppResponseDTO.<Page<VehicleBrandReportDTO>>builder()
                        .status(200)
                        .success(true)
                        .content(page)
                        .build());

        mockMvc.perform(get("/veiculos/relatorios/por-marca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.content[0].brand").value("Toyota"));
    }
}