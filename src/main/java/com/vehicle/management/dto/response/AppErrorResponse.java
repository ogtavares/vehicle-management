package com.vehicle.management.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppErrorResponse {
    private int status;
    private String message;
    private String details;
}
