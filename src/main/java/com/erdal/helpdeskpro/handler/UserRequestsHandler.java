package com.erdal.helpdeskpro.handler;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;

import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.util.JsonUtil;
public abstract class UserRequestsHandler {
	
	private final UserController userController;

	public UserRequestsHandler() {
		this.userController = new UserController();
	}
	
	
	// register handler
	protected void handleRegister(HttpExchange exchange) throws IOException {
		    try {
		        String body = readBody(exchange.getRequestBody());
		        UserDTO dto = JsonUtil.fromJson(body, UserDTO.class);

		        if (dto == null) {
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
	
	protected void handleGet(HttpExchange exchange) throws IOException {

	    String query = exchange.getRequestURI().getQuery();

	    try {
	        // ?id=5 varsa → tek user
	        if (query != null && query.startsWith("id=")) {
	            Long id = Long.parseLong(query.split("=")[1]);
	            var user = userController.findUserById(id);

	            if (user == null) {
	                sendResponse(exchange, 404, "User not found");
	                return;
	            }

	            String json = JsonUtil.toJson(user);
	            sendJson(exchange, 200, json);
	            return;
	        }

	        // ? yoksa → tüm kullanıcılar
	        var users = userController.findAllUsers();
	        String json = JsonUtil.toJson(users);
	        sendJson(exchange, 200, json);

	    } catch (Exception e) {
	        e.printStackTrace();
	        sendResponse(exchange, 500, "Internal Server Error");
	    }
	}
		
	protected void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

	private String readBody(InputStream is) throws IOException {
		return new String(is.readAllBytes(), StandardCharsets.UTF_8);
	}
	private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
	    exchange.getResponseHeaders().add("Content-Type", "application/json");
	    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
	    exchange.sendResponseHeaders(status, bytes.length);
	    try (OutputStream os = exchange.getResponseBody()) {
	        os.write(bytes);
	    }
	}
	

}
