package com.vehicle.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AppResponseDTO<T> {
    private Integer status;
    private Boolean success;
    private String message;
    private Map<String, Object> parameters;
    private T content;

    public static <T> AppResponseDTO<T> getSuccessResponse(String message){
        return AppResponseDTO.<T>builder().message(message).status(200).success(true).build();
    }

    public static <T> AppResponseDTO<T> getSuccessResponse(String message, Map<String, Object> parameters){
        return AppResponseDTO.<T>builder().message(message).parameters(parameters).status(200).success(true).build();
    }
}
