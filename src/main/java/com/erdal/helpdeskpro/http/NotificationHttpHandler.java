package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.NotificationController;
import com.erdal.helpdeskpro.domain.Notification;
import com.erdal.helpdeskpro.domain.User;
import java.util.stream.Collectors;
import com.erdal.helpdeskpro.dtos.NotificationDTO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class NotificationHttpHandler implements HttpHandler {

    private final NotificationController notificationController;
    private final AuthService authService;

    public NotificationHttpHandler(
            NotificationController notificationController,
            AuthService authService) {
        this.notificationController = notificationController;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = authenticateRequest(exchange);

            if (user == null) {
                sendError(exchange, 401, "Unauthorized");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            // GET /notifications/unread-count
            if ("GET".equalsIgnoreCase(method)
                    && "/notifications/unread-count".equals(path)) {
                handleUnreadCount(exchange, user);
                return;
            }

            // PUT /notifications/read-all
            if ("PUT".equalsIgnoreCase(method)
                    && "/notifications/read-all".equals(path)) {
                handleMarkAllAsRead(exchange, user);
                return;
            }

            // PUT /notifications/{id}/read
            if ("PUT".equalsIgnoreCase(method)
                    && path.matches("/notifications/\\d+/read")) {
                handleMarkAsRead(exchange, path, user);
                return;
            }

            // GET /notifications
            if ("GET".equalsIgnoreCase(method)
                    && "/notifications".equals(path)) {
                handleGetNotifications(exchange, user);
                return;
            }
            
         // DELETE /notifications/clear-all
            if ("DELETE".equalsIgnoreCase(method)
                    && "/notifications/clear-all".equals(path)) {
                handleDeleteAllNotifications(exchange, user);
                return;
            }

            // DELETE /notifications/{id}
            if ("DELETE".equalsIgnoreCase(method)
                    && path.matches("/notifications/\\d+")) {
                handleDeleteNotification(exchange, path, user);
                return;
            }

            sendError(exchange, 404, "Endpoint not found");

        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal Server Error");
        } finally {
            exchange.close();
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

 // GET /notifications
    private void handleGetNotifications(
            HttpExchange exchange, User user) throws IOException {

        List<Notification> notifications =
                notificationController.getMyNotifications(user);

        List<NotificationDTO> response = notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(response)
        );
    }

    // GET /notifications/unread-count
    private void handleUnreadCount(
            HttpExchange exchange, User user) throws IOException {

        long count = notificationController.getUnreadCount(user);

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(Map.of("unreadCount", count))
        );
    }

    // PUT /notifications/{id}/read
    private void handleMarkAsRead(
            HttpExchange exchange,
            String path,
            User user) throws IOException {

        String[] parts = path.split("/");
        Long notificationId = Long.parseLong(parts[2]);

        boolean updated =
                notificationController.markAsRead(notificationId, user);

        if (!updated) {
            sendError(exchange, 404, "Notification not found");
            return;
        }

        exchange.sendResponseHeaders(204, -1);
    }

    // PUT /notifications/read-all
    private void handleMarkAllAsRead(
            HttpExchange exchange, User user) throws IOException {

        int updated = notificationController.markAllAsRead(user);

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(Map.of("updatedCount", updated))
        );
    }

    private void sendError(
            HttpExchange exchange,
            int status,
            String message) throws IOException {

        JsonUtil.sendJson(
                exchange,
                status,
                JsonUtil.toJson(Map.of("error", message))
        );
    }
    
 // DELETE /notifications/{id}
    private void handleDeleteNotification(
            HttpExchange exchange,
            String path,
            User user) throws IOException {

        String[] parts = path.split("/");
        Long notificationId = Long.parseLong(parts[2]);

        boolean deletedOrHidden =
                notificationController.deleteNotification(notificationId, user);

        if (!deletedOrHidden) {
            sendError(exchange, 404, "Notification not found");
            return;
        }

        exchange.sendResponseHeaders(204, -1);
    }

    // DELETE /notifications/clear-all
    private void handleDeleteAllNotifications(
            HttpExchange exchange,
            User user) throws IOException {

        int deletedOrHidden =
                notificationController.deleteAllNotifications(user);

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(Map.of("updatedCount", deletedOrHidden))
        );
    }
};