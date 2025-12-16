package com.vehicle.management.security.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Usuário é obrigatório")
    @JsonProperty("usuario")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @JsonProperty("senha")
    private String password;

    @JsonProperty("perfil")
    private String role;
}