package com.vehicle.management.mapper;

import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
public final class VehicleSortMapper {
    public static final Map<String, String> SORT_FIELDS = Map.of(
            "placa", "plate",
            "marca", "brand",
            "ano", "vehicleYear",
            "cor", "color",
            "preco", "price",
            "ativo", "active"
    );
}