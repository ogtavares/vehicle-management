package com.vehicle.management.security.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {
    @NotBlank(message = "Usuário é obrigatório")
    @JsonProperty("usuario")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @JsonProperty("senha")
    private String password;
}

