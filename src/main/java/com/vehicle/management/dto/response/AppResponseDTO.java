package com.vehicle.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(description = "Envelope padrão de resposta da API")
public class AppResponseDTO<T> {

    @Schema(description = "Código HTTP retornado", example = "200")
    private int status;

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
    private Boolean success;

    @Schema(description = "Mensagem informativa da operação", example = "Operação realizada com sucesso")
    private String message;

    @Schema(description = "Parâmetros utilizados durante a requisição", example = "'cor': 'azul'")
    private Map<String, Object> parameters;

    @Schema(description = "Conteúdo principal da resposta")
    private T content;

    public static <T> AppResponseDTO<T> getSuccessResponse(String message) {
        return AppResponseDTO.<T>builder()
                .message(message)
                .status(200)
                .success(true)
                .build();
    }

    public static <T> AppResponseDTO<T> getSuccessResponse(String message, Map<String, Object> parameters) {
        return AppResponseDTO.<T>builder()
                .message(message)
                .parameters(parameters)
                .status(200)
                .success(true)
                .build();
    }
}
