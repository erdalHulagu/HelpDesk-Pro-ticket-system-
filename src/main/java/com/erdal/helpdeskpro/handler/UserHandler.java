package com.erdal.helpdeskpro.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.util.JsonUtil;

public class UserHandler implements HttpHandler {

    private final UserController userController;

    public UserHandler() {
        this.userController = new UserController();
    }

   
	@SuppressWarnings("unused")
	@Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "POST, OPTIONS, GET, DELETE, PUT, PATCH");
        headers.add("Access-Control-Allow-Headers", "Content-Type");

        // Preflight request (CORS)
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String rawQuery = exchange.getRequestURI().getQuery();
        System.out.println("[UserHandler] " + method + " " + path + (rawQuery != null ? "?" + rawQuery : ""));

        // Sadece POST request
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }else if ("GET".equalsIgnoreCase(exchange)) {
        	
        	//Burada kaldin  ......................
        	//....................................
			
		}

        try {
            String body = readBody(exchange.getRequestBody());
            UserDTO dto = JsonUtil.fromJson(body, UserDTO.class);
            System.out.println("DTO: " + dto.getUsername() + ", " + dto.getEmail());

            if(dto == null) {
                sendResponse(exchange, 400, "Invalid JSON");
                return;
            }

            userController.register(dto);

            sendResponse(exchange, 201, "User registered successfully");

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readBody(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}