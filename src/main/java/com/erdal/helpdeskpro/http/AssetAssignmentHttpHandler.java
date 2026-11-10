
package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.BaseAuthHandler;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.AssetAssignmentController;
import com.erdal.helpdeskpro.domain.AssetAssignment;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AssetAssignmentHttpHandler
        extends BaseAuthHandler implements HttpHandler {

    private final AssetAssignmentController assignmentController;

    public AssetAssignmentHttpHandler(
            AssetAssignmentController assignmentController,
            AuthService authService
    ) {
        super(authService);
        this.assignmentController = assignmentController;
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

            if (!isAuthorized(authenticatedUser)) {
                sendJson(exchange, 403,
                        "{\"error\":\"Access denied\"}");
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equalsIgnoreCase(method)
                    && "/asset-assignments".equals(path)) {

                handleGetAll(exchange);

            } else if ("POST".equalsIgnoreCase(method)
                    && "/asset-assignments".equals(path)) {

                handleAssign(exchange, authenticatedUser);

            } else if (path.matches(
                    "/asset-assignments/user/\\d+")
                    && "GET".equalsIgnoreCase(method)) {

                Long userId = extractId(path);
                handleGetByUser(exchange, userId);

            } else if (path.matches(
                    "/asset-assignments/asset/\\d+")
                    && "GET".equalsIgnoreCase(method)) {

                Long assetId = extractId(path);
                handleGetByAsset(exchange, assetId);

            } else if (path.matches(
                    "/asset-assignments/\\d+/return")
                    && "PUT".equalsIgnoreCase(method)) {

                Long assignmentId = extractReturnId(path);
                handleReturn(exchange, assignmentId);

            } else if (path.matches(
                    "/asset-assignments/\\d+")
                    && "GET".equalsIgnoreCase(method)) {

                Long id = extractId(path);
                handleGetById(exchange, id);

            } else {
                sendJson(exchange, 404,
                        "{\"error\":\"Endpoint not found\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();

            sendJson(exchange, 500,
                    "{\"error\":\"Internal server error\"}");
        }
    }

    private boolean isAuthorized(User user) {
        return user.getRole() == Role.ADMIN
                || user.getRole() == Role.IT_SUPPORT;
    }

    private void handleGetAll(HttpExchange exchange)
            throws IOException {

        List<AssetAssignment> assignments =
                assignmentController.getAllAssignments();

        sendJson(exchange, 200, JsonUtil.toJson(assignments));
    }

    private void handleAssign(
            HttpExchange exchange,
            User authenticatedUser
    ) throws IOException {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        AssetAssignment assignment =
                JsonUtil.fromJson(body, AssetAssignment.class);

        if (assignment == null || assignment.getAsset() == null
                || assignment.getAsset().getId() == null
                || assignment.getUser() == null
                || assignment.getUser().getId() == null) {

            sendJson(exchange, 400,
                    "{\"error\":\"Asset and user are required\"}");
            return;
        }

        // assignedBy bilgisi token ile doğrulanmış kullanıcıdan alınır.
        assignment.setAssignedBy(authenticatedUser);

        // Yeni atama oluşturulur.
        assignment.setId(null);
        assignment.setReturnedAt(null);

        assignmentController.assignAsset(assignment);

        sendJson(exchange, 201,
                "{\"message\":\"Asset assigned successfully\"}");
    }

    private void handleGetById(
            HttpExchange exchange,
            Long id
    ) throws IOException {

        AssetAssignment assignment =
                assignmentController.getAssignmentById(id);

        if (assignment == null) {
            sendJson(exchange, 404,
                    "{\"error\":\"Assignment not found\"}");
            return;
        }

        sendJson(exchange, 200, JsonUtil.toJson(assignment));
    }

    private void handleGetByUser(
            HttpExchange exchange,
            Long userId
    ) throws IOException {

        List<AssetAssignment> assignments =
                assignmentController.getAssignmentsByUserId(userId);

        sendJson(exchange, 200, JsonUtil.toJson(assignments));
    }

    private void handleGetByAsset(
            HttpExchange exchange,
            Long assetId
    ) throws IOException {

        List<AssetAssignment> assignments =
                assignmentController.getAssignmentsByAssetId(assetId);

        sendJson(exchange, 200, JsonUtil.toJson(assignments));
    }

    private void handleReturn(
            HttpExchange exchange,
            Long assignmentId
    ) throws IOException {

        AssetAssignment assignment =
                assignmentController.getAssignmentById(assignmentId);

        if (assignment == null) {
            sendJson(exchange, 404,
                    "{\"error\":\"Assignment not found\"}");
            return;
        }

        assignmentController.returnAsset(assignmentId);

        sendJson(exchange, 200,
                "{\"message\":\"Asset returned successfully\"}");
    }

    private Long extractId(String path) {

        String[] parts = path.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private Long extractReturnId(String path) {

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
}
