package com.erdal.helpdeskpro.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import com.erdal.helpdeskpro.controller.UserController;

public class UserHandler extends UserRequestsHandler implements HttpHandler {

	private final UserController userController;

	public UserHandler() {
		this.userController = new UserController();
	}

	
	@Override
	public void handle(HttpExchange exchange) throws IOException {

	    Headers headers = exchange.getResponseHeaders();
	    headers.add("Access-Control-Allow-Origin", "*");
	    headers.add("Access-Control-Allow-Methods", "POST, OPTIONS, GET, PUT, PATCH");
	    headers.add("Access-Control-Allow-Headers", "Content-Type");

	    String method = exchange.getRequestMethod();
	    String path = exchange.getRequestURI().getPath();

	    // CORS
	    if ("OPTIONS".equalsIgnoreCase(method)) {
	        exchange.sendResponseHeaders(204, -1);
	        return;
	    }

	    // REGISTER
	    if ("POST".equalsIgnoreCase(method) && path.equals("/register")) {
	        handleRegister(exchange);
	        return;
	    }

	    // GET
	    if ("GET".equalsIgnoreCase(method)) {


	        // /users?email=...
	        if (path.equals("/users") && exchange.getRequestURI().getQuery() != null) {
	            handleGetUserByEmail(exchange);
	            return;
	        }
	        // /users
	        if (path.equals("/users")) {
	            handleGetAllUsers(exchange);
	            return;
	        }

	        // /users/{id}
	        if (path.matches("/users/\\d+")) {
	            handleGetUserById(exchange);
	            return;
	        }


	        sendResponse(exchange, 404, "Not Found");
	        return;
	    }

	    // UPDATE
	    if ("PUT".equalsIgnoreCase(method) && path.matches("/users")) {
	        handleUpdateUser(exchange);
	        return;
	    }
	    if ("DELETE".equalsIgnoreCase(method) && path.matches("/users/\\d+")) {
	        handleDeleteUser(exchange);
	        return;
	    }
	    sendResponse(exchange, 405, "Method Not Allowed");
	}
}