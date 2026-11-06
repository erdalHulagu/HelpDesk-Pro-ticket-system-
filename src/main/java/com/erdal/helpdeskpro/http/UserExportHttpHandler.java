
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

            // Read date filters
            String startDateParam = getQueryParam(exchange, "startDate");
            String endDateParam = getQueryParam(exchange, "endDate");

            LocalDateTime startDate = null;
            LocalDateTime endDate = null;

            try {
                if (startDateParam != null && !startDateParam.isBlank()) {
                    startDate = LocalDate.parse(startDateParam).atStartOfDay();
                }

                if (endDateParam != null && !endDateParam.isBlank()) {
                    endDate = LocalDate.parse(endDateParam).atTime(LocalTime.MAX);
                }

            } catch (java.time.format.DateTimeParseException e) {
                sendError(exchange, 400,
                        "Invalid date format. Use yyyy-MM-dd.");
                return;
            }

            if (startDate != null && endDate != null
                    && startDate.isAfter(endDate)) {
                sendError(exchange, 400,
                        "Start date cannot be after end date.");
                return;
            }

            // Fetch filtered users
            List<User> users = userService.getUsersForExport(
                    currentUser,
                    startDate,
                    endDate
            );

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

    private String getQueryParam(HttpExchange exchange, String paramName) {

        String query = exchange.getRequestURI().getRawQuery();

        if (query == null || query.isBlank()) {
            return null;
        }

        for (String param : query.split("&")) {

            String[] parts = param.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0],
                    StandardCharsets.UTF_8
            );

            if (key.equals(paramName)) {
                return parts.length > 1
                        ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                        : "";
            }
        }

        return null;
    }

    private void sendError(
            HttpExchange exchange,
            int statusCode,
            String message) throws IOException {

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

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
