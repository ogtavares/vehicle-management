package com.vehicle.management.security.user.service.impl;

import com.vehicle.management.model.enums.Role;
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
            throw new IllegalArgumentException("Usuário já existe");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(Role.valueOf(dto.getRole())));

        userRepository.save(user);
    }
}