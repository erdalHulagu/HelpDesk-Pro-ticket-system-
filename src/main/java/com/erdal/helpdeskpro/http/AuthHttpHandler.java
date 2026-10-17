
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.AuthController;
import com.erdal.helpdeskpro.web.request.LoginRequest;
import com.erdal.helpdeskpro.web.request.RegisterRequest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AuthHttpHandler implements HttpHandler {

    private final AuthController authController;
    private final ObjectMapper objectMapper;
    private final AuthService authService;

    public AuthHttpHandler(
            AuthController authController,
            AuthService authService) {

        this.authController = authController;
        this.authService = authService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            // ---------------- POST /auth/register ----------------
            if ("POST".equalsIgnoreCase(method)
                    && path.equals("/auth/register")) {

                handleRegister(exchange);
                return;
            }

            // ---------------- POST /auth/login ----------------
            if ("POST".equalsIgnoreCase(method)
                    && path.equals("/auth/login")) {

                handleLogin(exchange);
                return;
            }

            // ---------------- POST /auth/logout ----------------
            if ("POST".equalsIgnoreCase(method)
                    && path.equals("/auth/logout")) {

                handleLogout(exchange);
                return;
            }

            // Tanımlanmamış endpoint
            if (path.equals("/auth/register")
                    || path.equals("/auth/login")
                    || path.equals("/auth/logout")) {

                exchange.getResponseHeaders().set("Allow", "POST");
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            sendResponse(exchange, 404, "Endpoint not found");

        } catch (JsonProcessingException e) {
            sendResponse(exchange, 400, "Invalid JSON");

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());

        } catch (Exception e) {
            // Ayrıntıları sunucu loguna yaz; istemciye gizle.
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    // ---------------- Helper methods ----------------

    private void handleRegister(HttpExchange exchange) throws Exception {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        RegisterRequest request =
                objectMapper.readValue(body, RegisterRequest.class);

        authController.register(request);

        sendResponse(exchange, 201, "User registered");
    }

    private void handleLogin(HttpExchange exchange) throws Exception {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        LoginRequest request =
                objectMapper.readValue(body, LoginRequest.class);

        var response = authController.login(request);

        JsonUtil.sendJson(
                exchange,
                200,
                objectMapper.writeValueAsString(response)
        );
    }

    private void handleLogout(HttpExchange exchange) throws IOException {

        String authorizationHeader =
                exchange.getRequestHeaders().getFirst("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            JsonUtil.sendJson(
                    exchange,
                    401,
                    "{\"message\":\"Missing or invalid Authorization header\"}"
            );
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        if (token.isBlank()) {
        	 JsonUtil.sendJson(
                    exchange,
                    401,
                    "{\"message\":\"Missing or invalid token\"}"
            );
            return;
        }

        // Tokenın geçerliliğini kontrol et.
        if (authService.authenticate(token) == null) {
        	 JsonUtil.sendJson(
                    exchange,
                    401,
                    "{\"message\":\"Invalid or expired token\"}"
            );
            return;
        }

        // Geçerli tokenı iptal et.
        authService.logout(token);

        JsonUtil.sendJson(
                exchange,
                200,
                "{\"message\":\"Logged out successfully\"}"
        );
    }

    private void sendResponse(
            HttpExchange exchange,
            int status,
            String message) throws IOException {

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

   
}
