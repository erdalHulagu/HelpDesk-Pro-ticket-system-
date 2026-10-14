package com.erdal.helpdeskpro.util;

import java.time.LocalDateTime;

import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.IllegalArgumentException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

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
}