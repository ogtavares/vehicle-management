package com.vehicle.management.security.user.dto.request;

import com.vehicle.management.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String role;
}