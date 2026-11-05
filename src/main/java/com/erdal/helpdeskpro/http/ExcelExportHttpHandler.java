
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.domain.SecurityLog;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.service.ExcelExportService;
import com.erdal.helpdeskpro.service.SecurityLogService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ExcelExportHttpHandler implements HttpHandler {

    private final ExcelExportService excelExportService;
    private final SecurityLogService securityLogService;
    private final AuthService authService;

    public ExcelExportHttpHandler(
            ExcelExportService excelExportService,
            SecurityLogService securityLogService,
            AuthService authService) {

        this.excelExportService = excelExportService;
        this.securityLogService = securityLogService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            // Only GET is allowed
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            // Authenticate user
            User user = authenticateRequest(exchange);

            if (user == null) {
                sendError(exchange, 401, "Unauthorized");
                return;
            }

            // Only ADMIN can export security logs
            if (user.getRole() != Role.ADMIN) {
                sendError(exchange, 403, "Access denied. Admin only.");
                return;
            }

            String path = exchange.getRequestURI().getPath();

            if (!"/export/security-logs".equals(path)) {
                sendError(exchange, 404, "Endpoint not found");
                return;
            }

            exportSecurityLogs(exchange);

        } catch (Exception e) {
            e.printStackTrace();

            if (exchange.getResponseCode() == -1) {
                sendError(exchange, 500, "Internal Server Error");
            }
        }
    }

    private User authenticateRequest(HttpExchange exchange) {

        String header = exchange.getRequestHeaders()
                .getFirst("Authorization");

        if (header == null
                || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }

        String token = header.substring(7).trim();

        if (token.isEmpty()) {
            return null;
        }

        return authService.authenticate(token);
    }

    private void exportSecurityLogs(HttpExchange exchange)
            throws IOException {

        List<SecurityLog> logs = securityLogService.getAllLogs();

        List<String> headers = Arrays.asList(
                "ID",
                "Username",
                "Action",
                "IP Address",
                "Details",
                "Date & Time"
        );

        List<List<String>> data = new ArrayList<>();

        for (SecurityLog log : logs) {

            List<String> row = Arrays.asList(
                    String.valueOf(log.getId()),
                    safeString(log.getUsername()),
                    safeString(log.getAction()),
                    safeString(log.getIpAddress()),
                    safeString(log.getDetails()),
                    log.getCreatedAt() == null
                            ? ""
                            : log.getCreatedAt().toString()
            );

            data.add(row);
        }

        byte[] excelFile = excelExportService.exportToExcel(
                "Security Logs",
                headers,
                data
        );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        exchange.getResponseHeaders().set(
                "Content-Disposition",
                "attachment; filename=\"security-logs.xlsx\""
        );

        exchange.getResponseHeaders().set(
                "Cache-Control",
                "no-store"
        );

        exchange.sendResponseHeaders(200, excelFile.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(excelFile);
        }
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private void sendError(
            HttpExchange exchange,
            int status,
            String message) throws IOException {

        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", message);

        String json = new com.google.gson.Gson().toJson(error);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        byte[] response = json.getBytes(
                java.nio.charset.StandardCharsets.UTF_8
        );

        exchange.sendResponseHeaders(status, response.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}
