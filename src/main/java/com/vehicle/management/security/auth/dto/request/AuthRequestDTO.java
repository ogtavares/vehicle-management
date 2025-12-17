package com.vehicle.management.security.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(
            description = "Nome do usuário",
            example = "admin"
    )
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @JsonProperty("senha")
    @Schema(
            description = "Senha",
            example = "senhaSuperSegura123"
    )
    private String password;
}