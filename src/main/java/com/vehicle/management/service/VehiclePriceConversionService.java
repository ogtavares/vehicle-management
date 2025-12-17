package com.vehicle.management.service;

import java.math.BigDecimal;

public interface VehiclePriceConversionService {
    BigDecimal getUsdToBrlRate();
    BigDecimal convertBrlToUsd(BigDecimal brlAmount);
    BigDecimal convertUsdToBrl(BigDecimal usdAmount);
}
