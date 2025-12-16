package com.vehicle.management.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiclePriceConversionServiceImplTest {

    @InjectMocks
    private VehiclePriceConversionServiceImpl service;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void shouldReturnRateFromRedis() {
        when(valueOps.get("USD_BRL_RATE")).thenReturn("5.10");

        BigDecimal rate = service.getUsdToBrlRate();

        assertEquals(new BigDecimal("5.10"), rate);
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    void shouldFetchRateFromPrimaryApiWhenRedisIsEmpty() {
        when(valueOps.get("USD_BRL_RATE")).thenReturn(null);

        Map<String, Object> usdbrl = Map.of("bid", "5.30");
        Map<String, Map<String, Object>> response = Map.of("USDBRL", usdbrl);

        when(restTemplate.getForObject(
                contains("awesomeapi"), eq(Map.class)))
                .thenReturn(response);

        BigDecimal rate = service.getUsdToBrlRate();

        assertEquals(new BigDecimal("5.30"), rate);
        verify(valueOps).set(eq("USD_BRL_RATE"), eq("5.30"), any());
    }

    @Test
    void shouldUseFallbackWhenPrimaryApiFails() {
        when(valueOps.get("USD_BRL_RATE")).thenReturn(null);

        when(restTemplate.getForObject(
                contains("awesomeapi"), eq(Map.class)))
                .thenThrow(new RuntimeException("Primary API down"));

        Map<String, Object> rates = Map.of("BRL", 5.40);
        Map<String, Object> fallbackResponse = Map.of("rates", rates);

        when(restTemplate.getForObject(
                contains("frankfurter"), eq(Map.class)))
                .thenReturn(fallbackResponse);

        BigDecimal rate = service.getUsdToBrlRate();

        assertEquals(BigDecimal.valueOf(5.40), rate);
    }

    @Test
    void shouldThrowExceptionWhenAllApisFail() {
        when(valueOps.get("USD_BRL_RATE")).thenReturn(null);

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("API down"));

        assertThrows(RuntimeException.class, () -> service.getUsdToBrlRate());
    }

    @Test
    void shouldConvertBrlToUsdCorrectly() {
        when(valueOps.get("USD_BRL_RATE")).thenReturn("5.00");

        BigDecimal usd = service.convertBrlToUsd(new BigDecimal("10.00"));

        assertEquals(new BigDecimal("2.0000"), usd);
    }

    @Test
    void shouldConvertUsdToBrlCorrectly() {
        when(valueOps.get("USD_BRL_RATE")).thenReturn("5.00");

        BigDecimal brl = service.convertUsdToBrl(new BigDecimal("10.00"));

        assertEquals(new BigDecimal("50.00"), brl);
    }
}