
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            User currentUser = authenticateRequest(exchange);

            if (currentUser == null) {
                sendError(exchange, 401, "Unauthorized");
                return;
            }

            if (currentUser.getRole() != Role.ADMIN) {
                sendError(exchange, 403, "Access denied. Admin only.");
                return;
            }

            Map<String, String> params = parseQuery(
                    exchange.getRequestURI().getRawQuery()
            );

            LocalDateTime startDate = null;
            LocalDateTime endDate = null;

            try {
                String startDateParam = params.get("startDate");
                String endDateParam = params.get("endDate");

                if (startDateParam != null && !startDateParam.isBlank()) {
                    startDate = LocalDate.parse(startDateParam.trim())
                            .atStartOfDay();
                }

                if (endDateParam != null && !endDateParam.isBlank()) {
                    endDate = LocalDate.parse(endDateParam.trim())
                            .atTime(LocalTime.MAX);
                }

            } catch (DateTimeParseException e) {
                sendError(
                        exchange,
                        400,
                        "Invalid date format. Use yyyy-MM-dd."
                );
                return;
            }

            if (startDate != null && endDate != null
                    && startDate.isAfter(endDate)) {
                sendError(
                        exchange,
                        400,
                        "Start date cannot be after end date."
                );
                return;
            }

            List<SecurityLog> logs =
                    securityLogService.getLogsForExport(
                            startDate,
                            endDate
                    );

            List<String> headers = List.of(
                    "ID",
                    "Username",
                    "Action",
                    "IP Address",
                    "Details",
                    "Date & Time"
            );

            List<List<String>> data = new ArrayList<>();

            for (SecurityLog log : logs) {

                List<String> row = new ArrayList<>();

                row.add(String.valueOf(log.getId()));
                row.add(log.getUsername());
                row.add(log.getAction());
                row.add(log.getIpAddress());
                row.add(log.getDetails());

                row.add(
                        log.getCreatedAt() == null
                                ? ""
                                : log.getCreatedAt().format(DATE_FORMATTER)
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

            try (var outputStream = exchange.getResponseBody()) {
                outputStream.write(excelFile);
            }

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

    private Map<String, String> parseQuery(String query) {

        Map<String, String> params = new LinkedHashMap<>();

        if (query == null || query.isBlank()) {
            return params;
        }

        for (String pair : query.split("&")) {

            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0],
                    StandardCharsets.UTF_8
            );

            String value = parts.length > 1
                    ? URLDecoder.decode(
                            parts[1],
                            StandardCharsets.UTF_8
                    )
                    : "";

            params.put(key, value);
        }

        return params;
    }

    private void sendError(
            HttpExchange exchange,
            int status,
            String message) throws IOException {

        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", message);

        byte[] response = com.erdal.helpdeskpro.config.util.JsonUtil
                .toJson(error)
                .getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, response.length);

        try (var outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}
