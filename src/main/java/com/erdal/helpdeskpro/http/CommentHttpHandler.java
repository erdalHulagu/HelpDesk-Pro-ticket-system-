package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.controller.CommentController;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.CommentDTO;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class CommentHttpHandler implements HttpHandler {

    private final CommentController commentController;
    private final AuthService authService;

    public CommentHttpHandler(
            CommentController commentController,
            AuthService authService) {
        this.commentController = commentController;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            User authenticatedUser = authenticateRequest(exchange);

            if (authenticatedUser == null) {
                sendJson(exchange, 401,
                        "{\"error\":\"Unauthorized\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if ("POST".equalsIgnoreCase(method)
                    && path.equals("/comments")) {
                handleCreateComment(exchange, authenticatedUser);
                return;
            }

            if ("GET".equalsIgnoreCase(method)
                    && path.matches("/comments/ticket/\\d+")) {
                handleGetCommentsByTicket(exchange, authenticatedUser);
                return;
            }

            if ("DELETE".equalsIgnoreCase(method)
                    && path.matches("/comments/\\d+")) {
                handleDeleteComment(exchange, authenticatedUser);
                return;
            }

            sendJson(exchange, 404,
                    "{\"error\":\"Endpoint not found\"}");

        } catch (ResourceNotFoundExeption e) {
            sendJson(exchange, 404,
                    JsonUtil.toJson(Map.of("error", e.getMessage())));

        } catch (BadRequestExeption e) {
            sendJson(exchange, 403,
                    JsonUtil.toJson(Map.of("error", e.getMessage())));

        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400,
                    "{\"error\":\"Invalid request\"}");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500,
                    "{\"error\":\"Internal Server Error\"}");
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

    private void handleCreateComment(
            HttpExchange exchange, User user) throws IOException {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

        CommentDTO commentDTO =
                JsonUtil.fromJson(body, CommentDTO.class);

        commentController.addComment(commentDTO, user);

        sendJson(exchange, 201, JsonUtil.toJson(commentDTO));
    }

    private void handleGetCommentsByTicket(
            HttpExchange exchange, User user) throws IOException {

        String path = exchange.getRequestURI().getPath();
        Long ticketId = Long.parseLong(
                path.substring("/comments/ticket/".length()));

        List<CommentDTO> comments =
                commentController.getCommentsByTicket(ticketId, user);

        sendJson(exchange, 200, JsonUtil.toJson(comments));
    }

    private void handleDeleteComment(
            HttpExchange exchange, User user) throws IOException {

        String path = exchange.getRequestURI().getPath();
        Long commentId = Long.parseLong(
                path.substring("/comments/".length()));

        commentController.deleteComment(commentId, user);

        exchange.sendResponseHeaders(204, -1);
    }

    private void sendJson(
            HttpExchange exchange, int status, String json)
            throws IOException {

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }
}
