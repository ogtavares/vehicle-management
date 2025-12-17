package com.vehicle.management.security.user.service.impl;

import com.vehicle.management.exception.ConflictException;
import com.vehicle.management.security.model.enums.Role;
import com.vehicle.management.security.user.dto.request.UserRequestDTO;
import com.vehicle.management.security.repository.UserRepository;
import com.vehicle.management.security.user.model.entity.User;
import com.vehicle.management.security.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserRequestDTO dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ConflictException("Usuário já existe");
        }

        Role role;

        if (dto.getRole() == null || dto.getRole().isBlank()) {
            role = Role.ROLE_USER;
        } else {
            try {
                String roleName = dto.getRole().toUpperCase();

                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }

                role = Role.valueOf(roleName);

            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Perfil inválido. Valores permitidos: USER, ADMIN"
                );
            }
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }
}
