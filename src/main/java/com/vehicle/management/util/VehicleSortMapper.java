package com.vehicle.management.util;

import java.util.Map;

public final class VehicleSortMapper {

    private VehicleSortMapper() {}

    public static final Map<String, String> SORT_FIELDS = Map.of(
            "placa", "plate",
            "marca", "brand",
            "ano", "vehicleYear",
            "cor", "color",
            "preco", "price",
            "ativo", "active"
    );
}