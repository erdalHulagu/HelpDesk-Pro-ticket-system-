
package com.erdal.helpdeskpro.config.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.IllegalArgumentException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

public class JsonUtil {

	private static final Gson gson = new GsonBuilder()
	        .registerTypeAdapter(
	                LocalDate.class,
	                new LocalDateTypeAdapter()
	        )
	        .registerTypeAdapter(
	                LocalDateTime.class,
	                new LocalDateTimeTypeAdapter()
	        )
	        .create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException(
                    ExceptionMessage.JSON_BODY_CANNOT_BE_EMPTY
            );
        }

        return gson.fromJson(json, clazz);
    }

    // Object verildiğinde JSON'a dönüştürür.
    public static void sendJson(
            HttpExchange exchange,
            int status,
            Object data) throws IOException {

        String json = gson.toJson(data);
        sendJson(exchange, status, json);
    }

    // Hazır JSON metnini HTTP response olarak gönderir.
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

        try {
            exchange.getResponseBody().write(bytes);
        } finally {
            exchange.close();
        }
    }
}