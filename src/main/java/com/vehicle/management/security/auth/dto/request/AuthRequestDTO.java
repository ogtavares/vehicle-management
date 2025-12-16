package com.vehicle.management.security.auth.dto.request;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}

