package com.vehicle.management.security.auth.service.impl;

import com.vehicle.management.model.enums.Role;
import com.vehicle.management.security.user.model.entity.User;
import com.vehicle.management.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("encoded-password");
        user.setActive(true);
        user.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());

        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );
        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        );
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername("unknown")
        );

        assertEquals(
                "Usuário não encontrado: unknown",
                exception.getMessage()
        );
    }
}