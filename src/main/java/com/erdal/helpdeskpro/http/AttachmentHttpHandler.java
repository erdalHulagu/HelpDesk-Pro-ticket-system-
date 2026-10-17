
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.erdal.helpdeskpro.controller.AttachmentController;
import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.auth.AuthService;

public class AttachmentHttpHandler implements HttpHandler {

    private final AttachmentController attachmentController;
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AttachmentHttpHandler(
            AttachmentController attachmentController,
            AuthService authService) {

        this.attachmentController = attachmentController;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User currentUser = authenticate(exchange);

            if (currentUser == null) {
                sendJson(exchange, 401, Map.of(
                        "error", "Unauthorized"
                ));
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("POST".equals(method) && "/attachments".equals(path)) {
                handleUpload(exchange, currentUser);
                return;
            }


            if ("GET".equals(method) && "/attachments".equals(path)) {
                handleList(exchange, currentUser);
                return;
            }

            Long attachmentId = extractAttachmentId(path);

            if (attachmentId == null) {
                sendJson(exchange, 404, Map.of(
                        "error", "Endpoint not found"
                ));
                return;
            }

            if ("GET".equals(method)) {
                handleDownload(exchange, attachmentId, currentUser);
                return;
            }

            if ("DELETE".equals(method)) {
                attachmentController.delete(attachmentId, currentUser);

                sendJson(exchange, 200, Map.of(
                        "message", "Attachment deleted successfully"
                ));
                return;
            }

            exchange.getResponseHeaders().set("Allow", "GET, POST, DELETE");
            sendJson(exchange, 405, Map.of(
                    "error", "Method not allowed"
            ));

        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, Map.of(
                    "error", safeMessage(e)
            ));

        } catch (SecurityException e) {
            sendJson(exchange, 403, Map.of(
                    "error", "Forbidden"
            ));

        } catch (Exception e) {
            e.printStackTrace();

            sendJson(exchange, 500, Map.of(
                    "error", "Internal server error"
            ));

        } finally {
            exchange.close();
        }
    }

    private User authenticate(HttpExchange exchange) {
        String authorizationHeader = exchange.getRequestHeaders()
                .getFirst("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authorizationHeader.substring(7).trim();

        if (token.isEmpty()) {
            return null;
        }

        return authService.authenticate(token);
    }

    private void handleUpload(
            HttpExchange exchange,
            User currentUser) throws IOException {

        Long ticketId = getTicketId(exchange);

        String fileNameHeader = exchange.getRequestHeaders()
                .getFirst("X-File-Name");

        String contentType = exchange.getRequestHeaders()
                .getFirst("Content-Type");

        if (fileNameHeader == null || contentType == null) {
            sendJson(exchange, 400, Map.of(
                    "error", "X-File-Name and Content-Type are required"
            ));
            return;
        }

        String fileName = URLDecoder.decode(
                fileNameHeader,
                StandardCharsets.UTF_8
        );

        Attachment attachment;

        try (InputStream inputStream = exchange.getRequestBody()) {
            attachment = attachmentController.upload(
                    ticketId,
                    fileName,
                    contentType,
                    inputStream,
                    currentUser
            );
        }

        sendJson(exchange, 201, toResponse(attachment));
    }

    private void handleList(
            HttpExchange exchange,
            User currentUser) throws IOException {

        Long ticketId = getTicketId(exchange);

        List<Attachment> attachments =
                attachmentController.findByTicket(ticketId, currentUser);

        List<Map<String, Object>> response = attachments.stream()
                .map(this::toResponse)
                .toList();

        sendJson(exchange, 200, response);
    }

    private void handleDownload(
            HttpExchange exchange,
            Long attachmentId,
            User currentUser) throws IOException {

        Attachment attachment = attachmentController.download(
                attachmentId,
                currentUser
        );

        Path filePath = Path.of(attachment.getFilePath());

        if (!Files.isRegularFile(filePath)) {
            sendJson(exchange, 404, Map.of(
                    "error", "File not found"
            ));
            return;
        }

        String encodedFileName = URLEncoder.encode(
                attachment.getFileName(),
                StandardCharsets.UTF_8
        ).replace("+", "%20");

        exchange.getResponseHeaders().set(
                "Content-Type",
                attachment.getFileType()
        );

        exchange.getResponseHeaders().set(
                "Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFileName
        );

        exchange.getResponseHeaders().set(
                "X-Content-Type-Options",
                "nosniff"
        );

        long fileSize = Files.size(filePath);
        exchange.sendResponseHeaders(200, fileSize);

        try (InputStream inputStream = Files.newInputStream(filePath);
             OutputStream outputStream = exchange.getResponseBody()) {

            inputStream.transferTo(outputStream);
        }
    }

    private Long getTicketId(HttpExchange exchange) {
        String query = exchange.getRequestURI().getRawQuery();

        if (query == null) {
            throw new IllegalArgumentException("ticketId is required");
        }

        for (String parameter : query.split("&")) {
            String[] pair = parameter.split("=", 2);

            if (pair.length == 2 && pair[0].equals("ticketId")) {
                try {
                    long id = Long.parseLong(pair[1]);

                    if (id <= 0) {
                        throw new IllegalArgumentException(
                                "ticketId must be positive"
                        );
                    }

                    return id;

                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Invalid ticketId"
                    );
                }
            }
        }

        throw new IllegalArgumentException("ticketId is required");
    }

    private Long extractAttachmentId(String path) {
        String prefix = "/attachments/";

        if (!path.startsWith(prefix)) {
            return null;
        }

        String idPart = path.substring(prefix.length());

        if (idPart.isBlank() || idPart.contains("/")) {
            return null;
        }

        try {
            long id = Long.parseLong(idPart);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Map<String, Object> toResponse(Attachment attachment) {
        return Map.of(
                "id", attachment.getId(),
                "ticketId", attachment.getTicket().getId(),
                "fileName", attachment.getFileName(),
                "fileType", attachment.getFileType(),
                "uploadedAt", attachment.getUploadedAt().toString()
        );
    }

    private void sendJson(
            HttpExchange exchange,
            int status,
            Object data) throws IOException {

        byte[] body = objectMapper.writeValueAsBytes(data);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.getResponseHeaders().set(
                "X-Content-Type-Options",
                "nosniff"
        );

        exchange.sendResponseHeaders(status, body.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        }
    }

    private String safeMessage(Exception e) {
        String message = e.getMessage();

        return message == null || message.isBlank()
                ? "Invalid request"
                : message;
    }
}
