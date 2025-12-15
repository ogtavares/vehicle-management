package com.vehicle.management.security.service.impl;

import com.vehicle.management.model.entity.User;
import com.vehicle.management.repository.UserRepository;
import com.vehicle.management.security.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        User user = optionalUser.get();
        Set<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );

    }
}