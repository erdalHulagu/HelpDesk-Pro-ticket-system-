
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.erdal.helpdeskpro.controller.AuthController;
import com.erdal.helpdeskpro.web.request.LoginRequest;
import com.erdal.helpdeskpro.web.request.RegisterRequest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AuthHttpHandler implements HttpHandler {

    private final AuthController authController;
    private final ObjectMapper objectMapper;

    public AuthHttpHandler(AuthController authController) {
        this.authController = authController;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Şimdilik yalnızca POST isteklerini kabul et.
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String path = exchange.getRequestURI().getPath();

        try {
            // JSON gövdesini oku.
            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            if ("/auth/register".equals(path)) {

                RegisterRequest request =
                        objectMapper.readValue(body, RegisterRequest.class);

                authController.register(request);

                sendResponse(exchange, 201, "User registered");

            } else if ("/auth/login".equals(path)) {

                LoginRequest request =
                        objectMapper.readValue(body, LoginRequest.class);

                var response = authController.login(request);

                // Token içeren JSON cevabını döndür.
                sendJson(exchange, 200, objectMapper.writeValueAsString(response));

            } else {
                sendResponse(exchange, 404, "Endpoint not found");
            }

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            sendResponse(exchange, 400, "Invalid JSON");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            // Ayrıntıları sunucu loguna yaz; istemciye gizle.
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(
            HttpExchange exchange,
            int status,
            String message) throws IOException {

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private void sendJson(
            HttpExchange exchange,
            int status,
            String json) throws IOException {

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
