package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.mapper.TicketMapper;
import com.erdal.helpdeskpro.domain.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * HTTP Handler for User REST API.
 * Supports GET, POST, PUT, DELETE
 */
public class UserHttpHandler implements HttpHandler {

    private final UserController userController;

    public UserHttpHandler(UserController userController) {
        this.userController = userController;
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

        UserDTO userDTO = userController.login(loginDTO.getUsername(), loginDTO.getPassword());

        String response = JsonUtil.toJson(userDTO);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleGetAllUsers(HttpExchange exchange) throws Exception {
        List<UserDTO> users = userController.findAllUsers();

        String response = JsonUtil.toJson(users);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleGetUserById(HttpExchange exchange) throws Exception {
        Long id = Long.parseLong(exchange.getRequestURI().getPath().split("/")[2]);
        UserDTO userDTO = userController.findUserById(id);

        String response = JsonUtil.toJson(userDTO);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleUpdateUser(HttpExchange exchange) throws Exception {
        Long id = Long.parseLong(exchange.getRequestURI().getPath().split("/")[2]);
        InputStream is = exchange.getRequestBody();
        UserDTO userDTO = JsonUtil.fromJson(new String(is.readAllBytes()), UserDTO.class);

        userDTO.setId(id); // make sure id is correct
        UserDTO updated = userController.updateUser(TicketMapper.ticketDTOtoTicket(userDTO)); // <-- adapt mapping

        String response = JsonUtil.toJson(updated);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleDeactivateUser(HttpExchange exchange) throws Exception {
        Long id = Long.parseLong(exchange.getRequestURI().getPath().split("/")[2]);
        userController.deActiveUser(id);

        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }
}
