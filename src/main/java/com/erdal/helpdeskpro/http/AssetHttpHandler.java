
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.BaseAuthHandler;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.AssetController;
import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.AssetStatus;
import com.erdal.helpdeskpro.enums.Role;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AssetHttpHandler extends BaseAuthHandler implements HttpHandler {

    private final AssetController assetController;

    public AssetHttpHandler(
            AssetController assetController,
            AuthService authService) {

        super(authService);
        this.assetController = assetController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {

            // All Asset endpoints require authentication.
            User currentUser = getAuthenticatedUser(exchange);

            // Only ADMIN and IT_SUPPORT can access asset inventory.
            if (!isAuthorized(currentUser)) {
                sendJson(exchange, 403,
                        "{\"error\":\"You are not authorized to access assets\"}");
                return;
            }

            // GET /assets
            if ("GET".equalsIgnoreCase(method) && path.equals("/assets")) {
                handleGetAllAssets(exchange);
                return;
            }

            // GET /assets/{id}
            if ("GET".equalsIgnoreCase(method)
                    && path.matches("/assets/\\d+")) {
                handleGetAssetById(exchange);
                return;
            }

            // GET /assets/tag/{assetTag}
            if ("GET".equalsIgnoreCase(method)
                    && path.startsWith("/assets/tag/")) {
                handleGetAssetByTag(exchange);
                return;
            }

            // GET /assets/status/{status}
            if ("GET".equalsIgnoreCase(method)
                    && path.startsWith("/assets/status/")) {
                handleGetAssetsByStatus(exchange);
                return;
            }

            // POST /assets
            if ("POST".equalsIgnoreCase(method)
                    && path.equals("/assets")) {
                handleCreateAsset(exchange);
                return;
            }

            // PUT /assets/{id}
            if ("PUT".equalsIgnoreCase(method)
                    && path.matches("/assets/\\d+")) {
                handleUpdateAsset(exchange);
                return;
            }

            // DELETE /assets/{id}
            if ("DELETE".equalsIgnoreCase(method)
                    && path.matches("/assets/\\d+")) {
                handleRetireAsset(exchange);
                return;
            }

            sendJson(exchange, 404,
                    "{\"error\":\"Endpoint not found\"}");

        } catch (Exception e) {

            sendJson(exchange, 400,
                    JsonUtil.toJson(
                            java.util.Collections.singletonMap(
                                    "error",
                                    e.getMessage() == null
                                            ? "Request failed"
                                            : e.getMessage()
                            )
                    ));
        }
    }

    private boolean isAuthorized(User user) {

        return user.getRole() == Role.ADMIN
                || user.getRole() == Role.IT_SUPPORT;
    }

    // GET /assets
    private void handleGetAllAssets(HttpExchange exchange)
            throws IOException {

        List<Asset> assets = assetController.getAllAssets();

        sendJson(exchange, 200, JsonUtil.toJson(assets));
    }

    // GET /assets/{id}
    private void handleGetAssetById(HttpExchange exchange)
            throws IOException {

        Long id = getIdFromPath(exchange);

        Asset asset = assetController.getAssetById(id);

        if (asset == null) {
            sendJson(exchange, 404,
                    "{\"error\":\"Asset not found\"}");
            return;
        }

        sendJson(exchange, 200, JsonUtil.toJson(asset));
    }

    // GET /assets/tag/{assetTag}
    private void handleGetAssetByTag(HttpExchange exchange)
            throws IOException {

        String assetTag = exchange.getRequestURI()
                .getPath()
                .substring("/assets/tag/".length());

        Asset asset = assetController.getAssetByTag(assetTag);

        if (asset == null) {
            sendJson(exchange, 404,
                    "{\"error\":\"Asset not found\"}");
            return;
        }

        sendJson(exchange, 200, JsonUtil.toJson(asset));
    }

    // GET /assets/status/{status}
    private void handleGetAssetsByStatus(HttpExchange exchange)
            throws IOException {

        String statusValue = exchange.getRequestURI()
                .getPath()
                .substring("/assets/status/".length());

        AssetStatus status = AssetStatus.valueOf(
                statusValue.toUpperCase()
        );

        List<Asset> assets =
                assetController.getAssetsByStatus(status);

        sendJson(exchange, 200, JsonUtil.toJson(assets));
    }

    // POST /assets
    private void handleCreateAsset(HttpExchange exchange)
            throws IOException {

        Asset asset = JsonUtil.fromJson(
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                ),
                Asset.class
        );

        asset.setId(null);

        assetController.createAsset(asset);

        sendJson(exchange, 201, JsonUtil.toJson(asset));
    }

    // PUT /assets/{id}
    private void handleUpdateAsset(HttpExchange exchange)
            throws IOException {

        Long id = getIdFromPath(exchange);

        Asset existingAsset = assetController.getAssetById(id);

        if (existingAsset == null) {
            sendJson(exchange, 404,
                    "{\"error\":\"Asset not found\"}");
            return;
        }

        Asset asset = JsonUtil.fromJson(
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                ),
                Asset.class
        );

        // Always use the ID from the URL.
        asset.setId(id);

        assetController.updateAsset(asset);

        sendJson(exchange, 200, JsonUtil.toJson(asset));
    }

    // DELETE /assets/{id}
    private void handleRetireAsset(HttpExchange exchange)
            throws IOException {

        Long id = getIdFromPath(exchange);

        Asset existingAsset = assetController.getAssetById(id);

        if (existingAsset == null) {
            sendJson(exchange, 404,
                    "{\"error\":\"Asset not found\"}");
            return;
        }

        assetController.retireAsset(id);

        sendJson(exchange, 200,
                "{\"message\":\"Asset retired successfully\"}");
    }

    private Long getIdFromPath(HttpExchange exchange) {

        String[] parts = exchange.getRequestURI()
                .getPath()
                .split("/");

        return Long.parseLong(parts[2]);
    }

    private void sendJson(
            HttpExchange exchange,
            int statusCode,
            String json) throws IOException {

        byte[] responseBytes =
                json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }
}
