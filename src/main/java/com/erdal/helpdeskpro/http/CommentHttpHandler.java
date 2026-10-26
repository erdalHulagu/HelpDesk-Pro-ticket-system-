package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.CommentController;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.CommentDTO;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
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
            	JsonUtil.sendJson(exchange, 401,
                        "{\"error\":\"Unauthorized\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            // POST /comments
            if ("POST".equalsIgnoreCase(method)
                    && path.equals("/comments")) {
                handleCreateComment(exchange, authenticatedUser);
                return;
            }

            // GET /comments/ticket/{ticketId}
            if ("GET".equalsIgnoreCase(method)
                    && path.matches("/comments/ticket/\\d+")) {
                handleGetCommentsByTicket(exchange, authenticatedUser);
                return;
            }

            // GET /comments/{commentId}
            if ("GET".equalsIgnoreCase(method)
                    && path.matches("/comments/\\d+")) {
                handleGetCommentById(exchange, authenticatedUser);
                return;
            }

            // DELETE /comments/{commentId}
            if ("DELETE".equalsIgnoreCase(method)
                    && path.matches("/comments/\\d+")) {
                handleDeleteComment(exchange, authenticatedUser);
                return;
            }

            JsonUtil.sendJson(exchange, 404,
                    "{\"error\":\"Endpoint not found\"}");

        } catch (ResourceNotFoundExeption e) {
        	JsonUtil.sendJson(exchange, 404,
                    JsonUtil.toJson(Map.of("error", e.getMessage())));

        } catch (BadRequestExeption e) {
        	JsonUtil.sendJson(exchange, 403,
                    JsonUtil.toJson(Map.of("error", e.getMessage())));

        } catch (IllegalArgumentException e) {
        	JsonUtil.sendJson(exchange, 400,
                    "{\"error\":\"Invalid request\"}");

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendJson(exchange, 500,
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
            StandardCharsets.UTF_8
        );

        CommentDTO commentDTO =
            JsonUtil.fromJson(body, CommentDTO.class);

        CommentDTO createdComment =
            commentController.addComment(commentDTO, user);

        JsonUtil.sendJson(
            exchange,
            201,
            JsonUtil.toJson(createdComment)
        );
    }

    private void handleGetCommentsByTicket(
            HttpExchange exchange, User user) throws IOException {

        String path = exchange.getRequestURI().getPath();
        Long ticketId = Long.parseLong(
                path.substring("/comments/ticket/".length()));

        List<CommentDTO> comments =
                commentController.getCommentsByTicket(ticketId, user);

        JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(comments));
    }

    // Yeni: Tek yorumu ID ile getir
    private void handleGetCommentById(
            HttpExchange exchange, User user) throws IOException {

        String path = exchange.getRequestURI().getPath();

        Long commentId = Long.parseLong(
                path.substring("/comments/".length()));

        CommentDTO comment =
                commentController.getCommentById(commentId, user);

        JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(comment));
    }

    private void handleDeleteComment(
            HttpExchange exchange, User user) throws IOException {

        String path = exchange.getRequestURI().getPath();
        Long commentId = Long.parseLong(
                path.substring("/comments/".length()));

        commentController.deleteComment(commentId, user);

        exchange.sendResponseHeaders(204, -1);
    }

    
}
