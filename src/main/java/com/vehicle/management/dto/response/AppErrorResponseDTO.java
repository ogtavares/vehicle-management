package com.vehicle.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Resposta padrão para erros da API")
public class AppErrorResponseDTO {
    @Schema(description = "Código HTTP do erro", example = "400")
    private int status;

    @Schema(description = "Mensagem de erro principal", example = "Erro de validação")
    private String message;

    @Schema(description = "Detalhes do erro", example = "Placa é obrigatória")
    private String details;
}
