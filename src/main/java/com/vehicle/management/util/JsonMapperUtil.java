package com.vehicle.management.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonMapperUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T convert(Object source, Class<T> targetClass) {
        return mapper.convertValue(source, targetClass);
    }

    public static <T> List<T> convertList(List<?> sourceList, Class<T> targetClass) {
        return mapper.convertValue(
                sourceList,
                mapper.getTypeFactory().constructCollectionType(List.class, targetClass)
        );
    }

    public static Map<String, Object> toNonNullMap(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }

        Map<String, Object> map = mapper.convertValue(obj, Map.class);
        map.values().removeIf(value -> value == null);
        return map;
    }
}