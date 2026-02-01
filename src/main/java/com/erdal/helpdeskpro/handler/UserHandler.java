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
	    headers.add("Access-Control-Allow-Methods", "POST, OPTIONS, GET");
	    headers.add("Access-Control-Allow-Headers", "Content-Type");

	    String method = exchange.getRequestMethod();

	    // CORS preflight
	    if ("OPTIONS".equalsIgnoreCase(method)) {
	        exchange.sendResponseHeaders(204, -1);
	        return;
	    }

	    if ("POST".equalsIgnoreCase(method)) {
	        handleRegister(exchange);
	        return;
	    }

	    if ("GET".equalsIgnoreCase(method)) {
	        handleGet(exchange);
	        return;
	    }

	    sendResponse(exchange, 405, "Method Not Allowed");
	
	}

	
}