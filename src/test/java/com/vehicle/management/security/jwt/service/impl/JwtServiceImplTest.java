package com.vehicle.management.security.jwt.service.impl;

import com.vehicle.management.security.jwt.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=chave-super-secreta-com-mais-de-32-chars",
        "jwt.expiration=3600000"
})
class JwtServiceImplTest {
    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateAndValidateToken() {

        UserDetails user = User.builder()
                .username("admin")
                .password("123")
                .roles("ADMIN")
                .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals("admin", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }
}