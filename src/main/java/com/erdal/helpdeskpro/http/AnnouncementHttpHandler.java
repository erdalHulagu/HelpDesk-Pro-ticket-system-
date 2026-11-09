
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.AnnouncementController;
import com.erdal.helpdeskpro.controller.AnnouncementReadController;
import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.domain.dtos.AnnouncementDTO;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.mapper.AnnouncementMapper;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AnnouncementHttpHandler implements HttpHandler {

    private final AnnouncementController announcementController;
    private final AnnouncementReadController announcementReadController;
    private final AuthService authService;

    public AnnouncementHttpHandler(
            AnnouncementController announcementController,
            AnnouncementReadController announcementReadController,
            AuthService authService) {

        this.announcementController = announcementController;
        this.announcementReadController = announcementReadController;
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

            // GET /announcements/my
            if ("GET".equalsIgnoreCase(method)
                    && "/announcements/my".equals(path)) {

                handleGetMyAnnouncements(exchange, user);
                return;
            }

            // GET /announcements
            if ("GET".equalsIgnoreCase(method)
                    && "/announcements".equals(path)) {

                handleGetAllAnnouncements(exchange, user);
                return;
            }

            // POST /announcements
            if ("POST".equalsIgnoreCase(method)
                    && "/announcements".equals(path)) {

                handleCreateAnnouncement(exchange, user);
                return;
            }

            // PUT /announcements/{id}/read
            if ("PUT".equalsIgnoreCase(method)
                    && path.matches("/announcements/\\d+/read")) {

                handleMarkAsRead(exchange, path, user);
                return;
            }

            // PUT /announcements/{id}
            if ("PUT".equalsIgnoreCase(method)
                    && path.matches("/announcements/\\d+")) {

                handleUpdateAnnouncement(exchange, path, user);
                return;
            }
            
            if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())
                    && "/announcements/read-all".equals(path)) {

                handleMarkAllAsRead(exchange);
                return;
            }

            // DELETE /announcements/{id}
            if ("DELETE".equalsIgnoreCase(method)
                    && path.matches("/announcements/\\d+")) {

                handleDeleteAnnouncement(exchange, path, user);
                return;
            }

            sendError(exchange, 404, "Endpoint not found");

        } catch (SecurityException e) {
            sendError(exchange, 403, e.getMessage());

        } catch (JsonSyntaxException | IllegalArgumentException e) {
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

    private void handleGetAllAnnouncements(
            HttpExchange exchange,
            User user) throws IOException {

        List<AnnouncementDTO> response =
                announcementController.getAllAnnouncements(user)
                        .stream()
                        .map(AnnouncementMapper::toDTO)
                        .collect(Collectors.toList());

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(response)
        );
    }

    private void handleGetMyAnnouncements(
            HttpExchange exchange,
            User user) throws IOException {

        List<Announcement> announcements =
                announcementController.getMyAnnouncements(user);

        Set<Long> readIds = new HashSet<>(
                announcementReadController.getReadAnnouncementIds(user)
        );

        List<AnnouncementDTO> response = announcements.stream()
                .map(announcement -> {

                    boolean read = readIds.contains(announcement.getId());

                    LocalDateTime readAt = read
                            ? announcementReadController.getReadAt(
                                    announcement.getId(), user)
                            : null;

                    return AnnouncementMapper.toDTOWithReadStatus(
                            announcement,
                            read,
                            readAt
                    );
                })
                .collect(Collectors.toList());

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(response)
        );
    }

    private void handleMarkAsRead(
            HttpExchange exchange,
            String path,
            User user) throws IOException {

        Long announcementId = extractReadId(path);

        announcementReadController.markAsRead(
                announcementId,
                user
        );

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(Map.of(
                        "message",
                        "Announcement marked as read"
                ))
        );
    }

    private void handleCreateAnnouncement(
            HttpExchange exchange,
            User user) throws IOException {

        AnnouncementRequest request = readRequest(exchange);

        Announcement announcement =
                announcementController.createAnnouncement(
                        request.title,
                        request.message,
                        parseRole(request.targetRole),
                        parseDate(request.expiresAt),
                        request.pinned,
                        user
                );

        AnnouncementDTO response =
                AnnouncementMapper.toDTO(announcement);

        JsonUtil.sendJson(
                exchange,
                201,
                JsonUtil.toJson(response)
        );
    }

    private void handleUpdateAnnouncement(
            HttpExchange exchange,
            String path,
            User user) throws IOException {

        Long announcementId = extractId(path);

        AnnouncementRequest request = readRequest(exchange);

        Announcement announcement =
                announcementController.updateAnnouncement(
                        announcementId,
                        request.title,
                        request.message,
                        parseRole(request.targetRole),
                        parseDate(request.expiresAt),
                        request.pinned,
                        request.active,
                        user
                );

        AnnouncementDTO response =
                AnnouncementMapper.toDTO(announcement);

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(response)
        );
    }
    
    private void handleMarkAllAsRead(HttpExchange exchange)
            throws IOException {

        User user = authenticateRequest(exchange);

        if (user == null) {
            JsonUtil.sendJson(
                    exchange,
                    401,
                    Map.of("message", "Unauthorized")
            );
            return;
        }

        announcementReadController.markAllAsRead(user);

        JsonUtil.sendJson(
                exchange,
                200,
                Map.of("message", "All announcements marked as read")
        );
    }

    private void handleDeleteAnnouncement(
            HttpExchange exchange,
            String path,
            User user) throws IOException {

        Long announcementId = extractId(path);

        boolean deleted =
                announcementController.deleteAnnouncement(
                        announcementId,
                        user
                );

        if (!deleted) {
            sendError(exchange, 404, "Announcement not found");
            return;
        }

        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    private AnnouncementRequest readRequest(
            HttpExchange exchange) throws IOException {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        return JsonUtil.fromJson(body, AnnouncementRequest.class);
    }

    private Long extractId(String path) {

        String[] parts = path.split("/");
        return Long.parseLong(parts[2]);
    }

    private Long extractReadId(String path) {

        String[] parts = path.split("/");
        return Long.parseLong(parts[2]);
    }

    private Role parseRole(String role) {

        if (role == null || role.isBlank()) {
            return null;
        }

        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid announcement target role: " + role
            );
        }
    }

    private LocalDateTime parseDate(String expiresAt) {

        if (expiresAt == null || expiresAt.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(expiresAt);
    }

    private void sendError(
            HttpExchange exchange,
            int status,
            String message) throws IOException {

        JsonUtil.sendJson(
                exchange,
                status,
                JsonUtil.toJson(Map.of(
                        "error",
                        message == null ? "Request failed" : message
                ))
        );
    }
    
    

    private static class AnnouncementRequest {

        private String title;
        private String message;
        private String targetRole;
        private String expiresAt;
        private boolean pinned;
        private boolean active = true;
    }
}
