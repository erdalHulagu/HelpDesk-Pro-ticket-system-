package com.erdal.helpdeskpro.handler;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.exception.UserExceptionMessage;
import com.erdal.helpdeskpro.request.UserRequest;
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
	
	
	protected void handleGetAllUsers(HttpExchange exchange) throws IOException {
	    try {
	        var users = userController.findAllUsers();
	        String json = JsonUtil.toJson(users);
	        sendJson(exchange, 200, json);

	    } catch (Exception e) {
	        e.printStackTrace();
	        sendResponse(exchange, 500, "Internal Server Error");
	    }
	}
	
	protected void handleGetUserById(HttpExchange exchange) throws IOException {
	    try {
	        String path = exchange.getRequestURI().getPath(); // /users/5
	        String idStr = path.substring(path.lastIndexOf("/") + 1);
	        Long id = Long.parseLong(idStr);

	        var user = userController.findUserById(id);

	        if (user == null) {
	            sendResponse(exchange, 404, "User not found");
	            return;
	        }

	        sendJson(exchange, 200, JsonUtil.toJson(user));

	    } catch (NumberFormatException e) {
	        sendResponse(exchange, 400, "Invalid ID");
	    } catch (Exception e) {
	        e.printStackTrace();
	        sendResponse(exchange, 500, "Internal Server Error");
	    }
	}
	protected void handleGetUserByEmail(HttpExchange exchange) throws IOException {
	    try {
	        String query = exchange.getRequestURI().getQuery(); // email=test@gmail.com

	        if (query == null || !query.startsWith("email=")) {
	            sendResponse(exchange, 400, "Email query param is required");
	            return;
	        }

	        String email = URLDecoder.decode(
	                query.substring("email=".length()),
	                StandardCharsets.UTF_8
	        );

	        var user = userController.findUserByEmail(email);

	        sendJson(exchange, 200, JsonUtil.toJson(user));

	    } catch (ResourceNotFoundExeption e) {
	        sendResponse(exchange, 404, e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        sendResponse(exchange, 500, "Internal Server Error");
	    }
	}
	protected void handleUpdateUser(HttpExchange exchange) throws IOException {
		try {
			String body = readBody(exchange.getRequestBody());
	        UserRequest userRequest = JsonUtil.fromJson(body, UserRequest.class);
			
	        if (userRequest==null) {
	        	sendResponse(exchange, 400, "Invalid JSON");
	            return;
			}
			
			var user= userController.updateUser(userRequest);
			
			 sendJson(exchange, 201, JsonUtil.toJson(user) );

	    } catch (Exception e) {
	        e.printStackTrace();
	        sendResponse(exchange, 500, "Internal Server Error");
	    
	}
	}
	protected void  handleDeleteUser(HttpExchange exchange) throws IOException {
		
		  try {
		        String path = exchange.getRequestURI().getPath(); // /users/5
		        String idStr = path.substring(path.lastIndexOf("/") + 1);
		        Long id = Long.parseLong(idStr);

		        userController.deleteUserById(id);
		         sendResponse(exchange, 201, "User deleted successfully");

		    } catch (NumberFormatException e) {
		        sendResponse(exchange, 400, "Invalid ID");
		    } catch (Exception e) {
		        e.printStackTrace();
		        
		        sendResponse(exchange, 500,UserExceptionMessage.USER_NOT_FOUND );
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
	
	
	private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {

	    if (json == null) {
	        json = "[]";
	    }

	    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

	    exchange.getResponseHeaders().add("Content-Type", "application/json");
	    exchange.sendResponseHeaders(statusCode, bytes.length);

	    try (OutputStream os = exchange.getResponseBody()) {
	        os.write(bytes);
	    }
	
	}
	

}
