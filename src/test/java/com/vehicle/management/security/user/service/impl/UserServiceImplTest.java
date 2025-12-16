package com.vehicle.management.security.user.service.impl;

import com.vehicle.management.model.enums.Role;
import com.vehicle.management.security.repository.UserRepository;
import com.vehicle.management.security.user.dto.request.UserRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateUserWithDefaultRole() {

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("user");
        dto.setPassword("123456");

        when(repository.existsByUsername("user")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");

        service.createUser(dto);

        verify(repository).save(argThat(user ->
                user.getRoles().contains(Role.ROLE_USER)
        ));
    }

    @Test
    void shouldThrowExceptionForInvalidRole() {

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("user");
        dto.setPassword("123456");
        dto.setRole("ROLE_INVALIDA");

        assertThrows(IllegalArgumentException.class,
                () -> service.createUser(dto));
    }
}
