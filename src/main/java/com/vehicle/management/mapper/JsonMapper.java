package com.vehicle.management.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class JsonMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> toNonNullMap(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }

        Map<String, Object> map = mapper.convertValue(obj, Map.class);
        map.values().removeIf(value -> value == null);
        return map;
    }
}