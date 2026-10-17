package com.erdal.helpdeskpro.config.util;

import java.io.IOException;
import java.time.LocalDateTime;

import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.IllegalArgumentException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.sun.net.httpserver.HttpExchange;
import java.nio.charset.StandardCharsets;

public class JsonUtil {

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class,
            (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                new JsonPrimitive(src.toString()))
        .create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessage.JSON_BODY_CANNOT_BE_EMPTY);
        }

        return gson.fromJson(json, clazz);
    }
    public static void sendJson(
            HttpExchange exchange,
            int status,
            String json) throws IOException {

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

/**
 * JSON utility for serializing/deserializing objects.
 */

//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    public static String toJson(Object obj) throws Exception {
//        return objectMapper.writeValueAsString(obj);
//    }
//
//    public static <T> T fromJson(String json, Class<T> cls) throws Exception {
//        return objectMapper.readValue(json, cls);
//    }
//    
//    
//    
//    
//}
}