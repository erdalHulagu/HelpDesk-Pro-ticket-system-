
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.service.ExcelExportService;
import com.erdal.helpdeskpro.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class UserExportHttpHandler implements HttpHandler {

    private final ExcelExportService excelExportService;
    private final UserService userService;
    private final AuthService authService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserExportHttpHandler(
            ExcelExportService excelExportService,
            UserService userService,
            AuthService authService) {

        this.excelExportService = excelExportService;
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            String authHeader = exchange.getRequestHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendError(exchange, 401, "Unauthorized");
                return;
            }

            String token = authHeader.substring(7);

            User currentUser = authService.authenticate(token);

            if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
                sendError(exchange, 403, "Access denied");
                return;
            }

            List<User> users = userService.getAllUsers(currentUser);

            List<String> headers = Arrays.asList(
                    "ID",
                    "Username",
                    "Email",
                    "Role",
                    "Active Status",
                    "Created At"
            );

            List<List<String>> data = new ArrayList<>();

            for (User user : users) {

                List<String> row = Arrays.asList(
                        String.valueOf(user.getId()),
                        user.getUsername() != null ? user.getUsername() : "",
                        user.getEmail() != null ? user.getEmail() : "",
                        user.getRole() != null ? user.getRole().name() : "",
                        user.isActive() ? "Active" : "Inactive",
                        user.getCreatedAt() != null
                                ? user.getCreatedAt().format(DATE_FORMATTER)
                                : ""
                );

                data.add(row);
            }

            byte[] excelFile = excelExportService.exportToExcel(
                    "Users",
                    headers,
                    data
            );

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );

            exchange.getResponseHeaders().set(
                    "Content-Disposition",
                    "attachment; filename=\"users.xlsx\""
            );

            exchange.getResponseHeaders().set("Cache-Control", "no-store");

            exchange.sendResponseHeaders(200, excelFile.length);

            try (var outputStream = exchange.getResponseBody()) {
                outputStream.write(excelFile);
            }

        } catch (Exception e) {

            e.printStackTrace();

            if (exchange.getResponseCode() == -1) {
                sendError(exchange, 500, "Failed to export users");
            }
        }
    }

    private void sendError(
            HttpExchange exchange,
            int statusCode,
            String message) throws IOException {

        byte[] response = message.getBytes("UTF-8");

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, response.length);

        try (var outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}
