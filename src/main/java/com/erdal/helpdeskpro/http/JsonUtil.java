package com.erdal.helpdeskpro.http;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON utility for serializing/deserializing objects.
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}

