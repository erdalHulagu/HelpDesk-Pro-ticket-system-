package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.controller.CommentController;
import com.erdal.helpdeskpro.dtos.CommentDTO;
import com.erdal.helpdeskpro.domain.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * HTTP Handler for Comment REST API.
 * Supports GET, POST, DELETE
 */
public class CommentHttpHandler implements HttpHandler {

    private final CommentController commentController;
    private final User authenticatedUser;

    public CommentHttpHandler(CommentController commentController, User authenticatedUser) {
        this.commentController = commentController;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            // ---------------- POST /comments ----------------
            if ("POST".equalsIgnoreCase(method) && path.equals("/comments")) {
                handleCreateComment(exchange);
                return;
            }

            // ---------------- GET /comments/ticket/{ticketId} ----------------
            if ("GET".equalsIgnoreCase(method) && path.matches("/comments/ticket/\\d+")) {
                handleGetCommentsByTicket(exchange);
                return;
            }

            // ---------------- DELETE /comments/{id} ----------------
            if ("DELETE".equalsIgnoreCase(method) && path.matches("/comments/\\d+")) {
                handleDeleteComment(exchange);
                return;
            }

            // 404 for unknown paths
            exchange.sendResponseHeaders(404, -1);

        } catch (Exception e) {
            String errorJson = "{\"error\":\"" + e.getMessage() + "\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, errorJson.getBytes().length);
            exchange.getResponseBody().write(errorJson.getBytes());
            exchange.close();
        }
    }

    // ---------------- Helper methods ----------------

    private void handleCreateComment(HttpExchange exchange) throws Exception {
        InputStream is = exchange.getRequestBody();
        CommentDTO commentDTO = JsonUtil.fromJson(new String(is.readAllBytes()), CommentDTO.class);

        commentController.addComment(commentDTO, authenticatedUser);

        String response = JsonUtil.toJson(commentDTO);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(201, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleGetCommentsByTicket(HttpExchange exchange) throws Exception {
        Long ticketId = Long.parseLong(exchange.getRequestURI().getPath().split("/")[3]);
        List<CommentDTO> comments = commentController.getCommentsByTicket(ticketId, authenticatedUser);

        String response = JsonUtil.toJson(comments);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleDeleteComment(HttpExchange exchange) throws Exception {
        Long commentId = Long.parseLong(exchange.getRequestURI().getPath().split("/")[2]);
        commentController.deleteComment(commentId, authenticatedUser);

        exchange.sendResponseHeaders(204, -1); // No content
        exchange.close();
    }
}
