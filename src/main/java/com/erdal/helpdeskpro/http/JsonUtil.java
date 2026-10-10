package com.erdal.helpdeskpro.http;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON utility for serializing/deserializing objects.
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T fromJson(String json, Class<T> cls) throws Exception {
        return objectMapper.readValue(json, cls);
    }
    
    
}

