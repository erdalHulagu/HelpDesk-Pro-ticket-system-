package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.mapper.UserMapper;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.auth.BaseAuthHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HTTP Handler for User REST API. Supports GET, POST, PUT, DELETE
 */
public class UserHttpHandler extends BaseAuthHandler implements HttpHandler {

	private final UserController userController;
	private final AuthService authService;

	public UserHttpHandler(UserController userController, AuthService authService) {
		super(authService);
		this.userController = userController;
		this.authService = authService;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String path = exchange.getRequestURI().getPath();
		String method = exchange.getRequestMethod();

		try {
			// ---------------- POST /users/register ----------------
			if ("POST".equalsIgnoreCase(method) && path.equals("/users/register")) {
				handleRegister(exchange);
				return;
			}

			// ---------------- POST /users/login ----------------
			if ("POST".equalsIgnoreCase(method) && path.equals("/users/login")) {
				handleLogin(exchange);
				return;
			}

			// ---------------- GET /users ----------------
			if ("GET".equalsIgnoreCase(method) && path.equals("/users")) {
				handleGetAllUsers(exchange);
				return;
			}

			// ---------------- GET /users/{id} ----------------
			if ("GET".equalsIgnoreCase(method) && path.matches("/users/\\d+")) {
				handleGetUserById(exchange);
				return;
			}

			// ---------------- PUT /users/{id} ----------------
			if ("PUT".equalsIgnoreCase(method) && path.matches("/users/\\d+")) {
				handleUpdateUser(exchange);
				return;
			}

			// ---------------- DELETE /users/{id} ----------------
			if ("DELETE".equalsIgnoreCase(method) && path.matches("/users/\\d+")) {
				handleDeactivateUser(exchange);
				return;
			}

			exchange.sendResponseHeaders(404, -1);

		} catch (Exception e) {
			String errorJson = "{\"error\":\"" + e.getMessage() + "\"}";
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(400, errorJson.getBytes().length);
			exchange.getResponseBody().write(errorJson.getBytes());
			exchange.close();
		}
	}

	// ---------------- Helper methods ----------------

	private void handleRegister(HttpExchange exchange) throws Exception {
		InputStream is = exchange.getRequestBody();
		UserDTO userDTO = JsonUtil.fromJson(new String(is.readAllBytes()), UserDTO.class);

		userController.register(userDTO);

		String response = JsonUtil.toJson(userDTO);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(201, response.getBytes().length);
		exchange.getResponseBody().write(response.getBytes());
		exchange.close();
	}

	private void handleLogin(HttpExchange exchange) throws Exception {
		InputStream is = exchange.getRequestBody();
		UserDTO loginDTO = JsonUtil.fromJson(new String(is.readAllBytes()), UserDTO.class);

		UserDTO userDTO = userController.login(loginDTO.getEmail(), loginDTO.getPassword());

		String response = JsonUtil.toJson(userDTO);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, response.getBytes().length);
		exchange.getResponseBody().write(response.getBytes());
		exchange.close();
	}

	private void handleGetAllUsers(HttpExchange exchange) throws Exception {

		User currentUser = getAuthenticatedUser(exchange);

		List<UserDTO> users = userController.getAllUsers(currentUser);

		String response = JsonUtil.toJson(users);
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

		exchange.sendResponseHeaders(200, responseBytes.length);
		exchange.getResponseBody().write(responseBytes);
		exchange.close();
	}

	private void handleGetUserById(HttpExchange exchange) throws Exception {
		
		User currentUser = getAuthenticatedUser(exchange);
		
		Long id = Long.parseLong(exchange.getRequestURI().getPath().split("/")[2]);
		// Admin değilse yalnızca kendi hesabını güncelleyebilir.
		if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {

			JsonUtil.sendJson(exchange, 403, "{\"error\":\"You can only update your own account\"}");
			return;
		}
		UserDTO userDTO = userController.findUserById(id);

		String response = JsonUtil.toJson(userDTO);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, response.getBytes().length);
		exchange.getResponseBody().write(response.getBytes());
		exchange.close();
	}

	private void handleUpdateUser(HttpExchange exchange) throws Exception {

		// İsteği yapan kullanıcıyı token üzerinden doğrula.
		User currentUser = getAuthenticatedUser(exchange);

		Long id = Long.parseLong(exchange.getRequestURI().getPath().split("/")[2]);
		
		// Admin değilse yalnızca kendi hesabını güncelleyebilir.
		if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {

			JsonUtil.sendJson(exchange, 403, "{\"error\":\"You can check your own account\"}");
			return;
		}

		InputStream is = exchange.getRequestBody();

		UserDTO userDTO = JsonUtil.fromJson(new String(is.readAllBytes(), StandardCharsets.UTF_8), UserDTO.class);
		
		if (currentUser.getRole() != Role.ADMIN) {
		    UserDTO existingUser = userController.findUserById(id);

		    if (userDTO.getRole() != null &&
		        userDTO.getRole() != existingUser.getRole()) {
		        JsonUtil.sendJson(
		            exchange,
		            403,
		            "{\"error\":\"Only admins can change user roles\"}"
		        );
		        return;
		    }
		}

		// İstek gövdesindeki ID'ye güvenme.
		userDTO.setId(id);

		UserDTO updated = userController.updateUser(UserMapper.userDTOtoUser(userDTO));

		String response = JsonUtil.toJson(updated);
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

		exchange.sendResponseHeaders(200, responseBytes.length);
		exchange.getResponseBody().write(responseBytes);
		exchange.close();
	}

	private void handleDeactivateUser(HttpExchange exchange) throws Exception {
		User user = getAuthenticatedUser(exchange);
		Long id = Long.parseLong(exchange.getRequestURI().getPath().split("/")[2]);
		userController.deActiveUser(user, id);

		exchange.sendResponseHeaders(204, -1);
		exchange.close();
	}

	
}
