package com.vehicle.management.security.user.service;

import com.vehicle.management.security.user.dto.request.UserRequestDTO;

public interface UserService {
    void createUser(UserRequestDTO dto);
}
