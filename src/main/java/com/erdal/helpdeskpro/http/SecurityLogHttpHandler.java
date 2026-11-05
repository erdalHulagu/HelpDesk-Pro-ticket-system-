
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.domain.SecurityLog;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.service.SecurityLogService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SecurityLogHttpHandler implements HttpHandler {

    private final SecurityLogService securityLogService;
    private final AuthService authService;

    public SecurityLogHttpHandler(
            SecurityLogService securityLogService,
            AuthService authService) {

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

            // Only ADMIN can access security logs
            if (user.getRole() != Role.ADMIN) {
                sendError(exchange, 403, "Access denied. Admin only.");
                return;
            }

            String path = exchange.getRequestURI().getPath();

            if (!"/security-logs".equals(path)) {
                sendError(exchange, 404, "Endpoint not found");
                return;
            }

            handleGetLogs(exchange);

        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal Server Error");
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

    private void handleGetLogs(HttpExchange exchange)
            throws IOException {

        Map<String, String> params = parseQuery(
                exchange.getRequestURI().getRawQuery()
        );

        String username = params.get("username");
        String action = params.get("action");

        List<SecurityLog> logs;

        if (username != null && !username.isBlank()) {
            logs = securityLogService.searchByUsername(username);

        } else if (action != null && !action.isBlank()) {
            logs = securityLogService.filterByAction(action);

        } else {
            logs = securityLogService.getAllLogs();
        }

        List<Map<String, Object>> response = logs.stream()
                .map(this::toResponseMap)
                .collect(Collectors.toList());

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(response)
        );
    }

    private Map<String, Object> toResponseMap(SecurityLog log) {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", log.getId());
        result.put("username", log.getUsername());
        result.put("action", log.getAction());
        result.put("ipAddress", log.getIpAddress());
        result.put("details", log.getDetails());
        result.put(
                "createdAt",
                log.getCreatedAt() == null
                        ? null
                        : log.getCreatedAt().toString()
        );

        return result;
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

        JsonUtil.sendJson(
                exchange,
                status,
                JsonUtil.toJson(error)
        );
    }
}
