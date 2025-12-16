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
public class AppResponse<T> {
    private int status;
    private Boolean success;
    private String message;
    private Map<String, Object> parameters;
    private T content;

    public static <T> AppResponse<T> getSuccessResponse(String message){
        return AppResponse.<T>builder().message(message).status(200).success(true).build();
    }

    public static <T> AppResponse<T> getSuccessResponse(String message, Map<String, Object> parameters){
        return AppResponse.<T>builder().message(message).parameters(parameters).status(200).success(true).build();
    }
}