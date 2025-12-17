package com.vehicle.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.management.security.auth.dto.request.AuthRequestDTO;
import com.vehicle.management.security.user.dto.request.UserRequestDTO;
import com.vehicle.management.dto.request.VehicleRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VehicleManagementIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fluxoCompleto_adminCriarListarDetalharVeiculo() throws Exception {
        // 1. Registrar usuário com perfil ADMIN
        UserRequestDTO register = new UserRequestDTO();
        register.setUsername("admin");
        register.setPassword("admin123");
        register.setRole("ROLE_ADMIN");

        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // 2. Autenticar usuário e obter JWT
        AuthRequestDTO login = new AuthRequestDTO("admin", "admin123");

        String tokenResponse = mockMvc.perform(post("/auth/logar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(tokenResponse).get("token").asText();

        // 3. Criar veículo usando token ADMIN
        VehicleRequestDTO vehicle = VehicleRequestDTO.builder()
                .brand("Toyota")
                .plate("ABC1234")
                .color("Preto")
                .vehicleYear(2022)
                .price(BigDecimal.valueOf(80000))
                .build();

        String createResponse = mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String vehicleId = objectMapper
                .readTree(createResponse)
                .get("content")
                .get("id")
                .asText();

        // 4. Listar veículos
        mockMvc.perform(get("/veiculos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 5. Detalhar veículo por ID
        mockMvc.perform(get("/veiculos/{id}", vehicleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.placa").value("ABC1234"));
    }
}