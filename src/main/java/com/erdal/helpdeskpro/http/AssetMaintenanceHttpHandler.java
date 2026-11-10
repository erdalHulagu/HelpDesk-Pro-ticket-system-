
package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.BaseAuthHandler;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.AssetMaintenanceController;
import com.erdal.helpdeskpro.domain.AssetMaintenance;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.AssetMaintenanceStatus;
import com.erdal.helpdeskpro.enums.Role;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AssetMaintenanceHttpHandler
        extends BaseAuthHandler implements HttpHandler {

    private final AssetMaintenanceController maintenanceController;

    public AssetMaintenanceHttpHandler(
            AssetMaintenanceController maintenanceController,
            AuthService authService
    ) {
        super(authService);
        this.maintenanceController = maintenanceController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            User authenticatedUser = getAuthenticatedUser(exchange);

            if (authenticatedUser == null) {
                sendJson(exchange, 401,
                        "{\"error\":\"Unauthorized\"}");
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            boolean staff = isStaff(authenticatedUser);

            // Employee, IT Support ve Admin arıza bildirimi oluşturabilir.
            if ("POST".equalsIgnoreCase(method)
                    && "/asset-maintenance".equals(path)) {

                handleCreate(exchange, authenticatedUser);
                return;
            }

            // Diğer bütün endpointler IT Support veya Admin içindir.
            if (!staff) {
                sendJson(exchange, 403,
                        "{\"error\":\"Access denied\"}");
                return;
            }
            
            if ("GET".equalsIgnoreCase(method)
                    && "/asset-maintenance/my".equals(path)) {

                handleGetMyIssues(exchange, authenticatedUser);
                return;
            }

            if ("GET".equalsIgnoreCase(method)
                    && "/asset-maintenance".equals(path)) {

                handleGetAll(exchange);

            } else if (path.matches(
                    "/asset-maintenance/asset/\\d+")
                    && "GET".equalsIgnoreCase(method)) {

                handleGetByAsset(exchange, extractId(path));

            } else if (path.matches(
                    "/asset-maintenance/status/[A-Z_]+")
                    && "GET".equalsIgnoreCase(method)) {

                String status = path.substring(
                        path.lastIndexOf("/") + 1
                );

                handleGetByStatus(exchange, status);

            } else if (path.matches(
                    "/asset-maintenance/\\d+/status")
                    && "PUT".equalsIgnoreCase(method)) {

                handleUpdateStatus(
                        exchange,
                        extractMaintenanceId(path)
                );

            } else if (path.matches(
                    "/asset-maintenance/\\d+")
                    && "GET".equalsIgnoreCase(method)) {

                handleGetById(exchange, extractId(path));

            } else {
                sendJson(exchange, 404,
                        "{\"error\":\"Endpoint not found\"}");
            }

        } catch (SecurityException e) {
            sendJson(exchange, 401,
                    "{\"error\":\"Unauthorized\"}");

        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400,
                    JsonUtil.toJson(Map.of(
                            "error",
                            e.getMessage() == null
                                    ? "Invalid request"
                                    : e.getMessage()
                    )));

        } catch (Exception e) {
            e.printStackTrace();

            sendJson(exchange, 500,
                    "{\"error\":\"Internal server error\"}");
        }
    }

    private boolean isStaff(User user) {
        return user.getRole() == Role.ADMIN
                || user.getRole() == Role.IT_SUPPORT;
    }

    private void handleGetAll(HttpExchange exchange)
            throws IOException {

        List<AssetMaintenance> maintenanceList =
                maintenanceController.getAllMaintenance();

        sendJson(exchange, 200,
                JsonUtil.toJson(maintenanceList));
    }

    private void handleCreate(
            HttpExchange exchange,
            User authenticatedUser
    ) throws IOException {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        AssetMaintenance maintenance =
                JsonUtil.fromJson(body, AssetMaintenance.class);

        if (maintenance == null
                || maintenance.getAsset() == null
                || maintenance.getAsset().getId() == null
                || maintenance.getTitle() == null
                || maintenance.getTitle().isBlank()
                || maintenance.getDescription() == null
                || maintenance.getDescription().isBlank()) {

            sendJson(exchange, 400,
                    "{\"error\":\"Asset, title and description are required\"}");
            return;
        }

        // Kullanıcı bilgisi token üzerinden alınır.
        maintenance.setReportedBy(authenticatedUser);

        // İstemciden gelen yönetim alanları kabul edilmez.
        maintenance.setId(null);
        maintenance.setAssignedTo(null);
        maintenance.setStatus(AssetMaintenanceStatus.OPEN);
        maintenance.setStartedAt(null);
        maintenance.setCompletedAt(null);

        maintenanceController.createMaintenance(maintenance);

        sendJson(exchange, 201,
                "{\"message\":\"Device issue reported successfully\"}");
    }

    private void handleGetById(
            HttpExchange exchange,
            Long id
    ) throws IOException {

        AssetMaintenance maintenance =
                maintenanceController.getMaintenanceById(id);

        if (maintenance == null) {
            sendJson(exchange, 404,
                    "{\"error\":\"Maintenance record not found\"}");
            return;
        }

        sendJson(exchange, 200,
                JsonUtil.toJson(maintenance));
    }

    private void handleGetByAsset(
            HttpExchange exchange,
            Long assetId
    ) throws IOException {

        List<AssetMaintenance> maintenanceList =
                maintenanceController.getMaintenanceByAssetId(assetId);

        sendJson(exchange, 200,
                JsonUtil.toJson(maintenanceList));
    }

    private void handleGetByStatus(
            HttpExchange exchange,
            String statusValue
    ) throws IOException {

        AssetMaintenanceStatus status;

        try {
            status = AssetMaintenanceStatus.valueOf(statusValue);
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400,
                    "{\"error\":\"Invalid maintenance status\"}");
            return;
        }

        List<AssetMaintenance> maintenanceList =
                maintenanceController.getMaintenanceByStatus(status);

        sendJson(exchange, 200,
                JsonUtil.toJson(maintenanceList));
    }

    private void handleUpdateStatus(
            HttpExchange exchange,
            Long id
    ) throws IOException {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        Map<?, ?> request =
                JsonUtil.fromJson(body, Map.class);

        if (request == null || request.get("status") == null) {
            sendJson(exchange, 400,
                    "{\"error\":\"Status is required\"}");
            return;
        }

        AssetMaintenanceStatus status;

        try {
            status = AssetMaintenanceStatus.valueOf(
                    request.get("status").toString()
                            .trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400,
                    "{\"error\":\"Invalid maintenance status\"}");
            return;
        }

        maintenanceController.updateMaintenanceStatus(id, status);

        sendJson(exchange, 200,
                "{\"message\":\"Maintenance status updated successfully\"}");
    }

    private Long extractId(String path) {
        String[] parts = path.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private Long extractMaintenanceId(String path) {
        String[] parts = path.split("/");
        return Long.parseLong(parts[2]);
    }

    private void sendJson(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
    
    private void handleGetMyIssues(
            HttpExchange exchange,
            User authenticatedUser
    ) throws IOException {

        List<AssetMaintenance> myIssues =
                maintenanceController.getMaintenanceByReporterId(
                        authenticatedUser.getId()
                );

        sendJson(exchange, 200, JsonUtil.toJson(myIssues));
    }
}
