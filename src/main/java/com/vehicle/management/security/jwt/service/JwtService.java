package com.vehicle.management.security.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails user);
}
