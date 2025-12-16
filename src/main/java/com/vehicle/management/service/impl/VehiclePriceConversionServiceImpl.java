package com.vehicle.management.service.impl;

import com.vehicle.management.service.VehiclePriceConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Service
public class VehiclePriceConversionServiceImpl implements VehiclePriceConversionService {
    private static final String USD_BRL_RATE_KEY = "USD_BRL_RATE";
    private static final Duration TTL = Duration.ofMinutes(10);

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal getUsdToBrlRate() {
        String rateStr = redisTemplate.opsForValue().get(USD_BRL_RATE_KEY);
        if (rateStr != null) {
            return new BigDecimal(rateStr);
        }

        try {
            Map<String, Map<String, Object>> response = restTemplate.getForObject(
                    "https://economia.awesomeapi.com.br/json/last/USD-BRL", Map.class);
            if (response != null && response.containsKey("USDBRL")) {
                Object bidObj = response.get("USDBRL").get("bid");
                if (bidObj instanceof String bidStr) {
                    BigDecimal rate = new BigDecimal(bidStr);
                    redisTemplate.opsForValue().set(USD_BRL_RATE_KEY, rate.toString(), TTL);
                    return rate;
                }
            }
        } catch (Exception e) {
            //fallback
            try {
                Map<String, Object> fallbackResponse = restTemplate.getForObject(
                        "https://api.frankfurter.app/latest?from=USD&to=BRL", Map.class);
                if (fallbackResponse != null && fallbackResponse.containsKey("rates")) {
                    Map<String, Object> rates = (Map<String, Object>) fallbackResponse.get("rates");
                    Object brlObj = rates.get("BRL");
                    if (brlObj instanceof Number brlRate) {
                        BigDecimal rate = BigDecimal.valueOf(brlRate.doubleValue());
                        redisTemplate.opsForValue().set(USD_BRL_RATE_KEY, rate.toString(), TTL);
                        return rate;
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException("Não foi possível obter a cotação do dólar", ex);
            }
        }

        throw new RuntimeException("Não foi possível obter a cotação do dólar");
    }

    public BigDecimal convertBrlToUsd(BigDecimal brlAmount) {
        BigDecimal rate = getUsdToBrlRate();
        return brlAmount.divide(rate, 4, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal convertUsdToBrl(BigDecimal usdAmount) {
        BigDecimal rate = getUsdToBrlRate();
        return usdAmount.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}