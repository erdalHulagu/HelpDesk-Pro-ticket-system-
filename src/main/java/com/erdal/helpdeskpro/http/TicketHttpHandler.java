
package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.controller.TicketController;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.TicketDTO;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TicketHttpHandler implements HttpHandler {

    private final TicketController ticketController;
    private final AuthService authService;

    public TicketHttpHandler(
            TicketController ticketController,
            AuthService authService) {
        this.ticketController = ticketController;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            // Token üzerinden isteği yapan kullanıcıyı bul.
            User authenticatedUser = authenticateRequest(exchange);

            if (authenticatedUser == null) {
                sendJson(exchange, 401,
                        "{\"error\":\"Unauthorized\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            // Ticket oluştur.
            if ("POST".equalsIgnoreCase(method)
                    && path.equals("/tickets")) {
                handleCreateTicket(exchange, authenticatedUser);
                return;
            }

            // Kullanıcının erişebildiği ticket listesi.
            if ("GET".equalsIgnoreCase(method)
                    && path.equals("/tickets")) {
                handleGetAllTickets(exchange, authenticatedUser);
                return;
            }

            // Kullanıcının kendi ticket listesi.
            if ("GET".equalsIgnoreCase(method)
                    && path.equals("/tickets/user")) {
                handleGetUsersTickets(exchange, authenticatedUser);
                return;
            }

            // Aktif ticket listesi.
            if ("GET".equalsIgnoreCase(method)
                    && path.equals("/tickets/active")) {
                handleGetActiveTickets(exchange, authenticatedUser);
                return;
            }

            // Tek ticket.
            if ("GET".equalsIgnoreCase(method)
                    && path.matches("/tickets/\\d+")) {
                handleGetTicketById(exchange, authenticatedUser);
                return;
            }

            // Ticket durumunu güncelle.
            if ("PUT".equalsIgnoreCase(method)
                    && path.matches("/tickets/\\d+/status")) {
                handleUpdateStatus(exchange, authenticatedUser);
                return;
            }

            // Ticket silme.
            if ("DELETE".equalsIgnoreCase(method)
                    && path.matches("/tickets/\\d+")) {
                handleDeleteTicket(exchange, authenticatedUser);
                return;
            }

            // Admin: silinenler dahil tüm ticket'lar.
            if ("GET".equalsIgnoreCase(method)
                    && path.equals("/admin/tickets")) {
                handleGetAllTicketsIncludingDeleted(
                        exchange, authenticatedUser);
                return;
            }

            // Admin: silinenler dahil tek ticket.
            if ("GET".equalsIgnoreCase(method)
                    && path.matches("/admin/tickets/\\d+")) {
                handleGetTicketIncludingDeleted(
                        exchange, authenticatedUser);
                return;
            }

            sendJson(exchange, 404,
                    "{\"error\":\"Endpoint not found\"}");

        } catch (Exception e) {
            e.printStackTrace();

            // Yanıt daha önce gönderilmediyse sunucu hatası döndür.
            try {
                sendJson(exchange, 500,
                        "{\"error\":\"Internal Server Error\"}");
            } catch (IOException ignored) {
                // Bağlantı zaten kapanmış olabilir.
            }
        } finally {
            exchange.close();
        }
    }

    private User authenticateRequest(HttpExchange exchange) {

        // Authorization: Bearer <token>
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

        // Token geçerliyse kullanıcıyı döndürür.
        return authService.authenticate(token);
    }

    private void handleCreateTicket(
            HttpExchange exchange, User user) throws IOException {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

        TicketDTO request = JsonUtil.fromJson(body, TicketDTO.class);
        TicketDTO created = ticketController.createTicket(request, user);

        sendJson(exchange, 201, JsonUtil.toJson(created));
    }

    private void handleGetAllTickets(
            HttpExchange exchange, User user) throws IOException {

        List<TicketDTO> tickets =
                ticketController.findAllActive(user);

        sendJson(exchange, 200, JsonUtil.toJson(tickets));
    }

    private void handleGetUsersTickets(
            HttpExchange exchange, User user) throws IOException {

        List<TicketDTO> tickets = ticketController.getusersTickets(user);

        sendJson(exchange, 200, JsonUtil.toJson(tickets));
    }

    private void handleGetTicketById(
            HttpExchange exchange, User user) throws IOException {

        String path = exchange.getRequestURI().getPath();
        Long id = Long.parseLong(path.substring("/tickets/".length()));

        TicketDTO ticket = ticketController.getTicket(id, user);

        sendJson(exchange, 200, JsonUtil.toJson(ticket));
    }

    private void handleUpdateStatus(
            HttpExchange exchange, User user) throws IOException {

        String path = exchange.getRequestURI().getPath();
        Long id = Long.parseLong(path.split("/")[2]);

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

        TicketDTO request = JsonUtil.fromJson(body, TicketDTO.class);

        ticketController.uspDateTicetsStatus(
                id, request.getStatus(), user);

        exchange.sendResponseHeaders(204, -1);
    }

    private void handleDeleteTicket(
            HttpExchange exchange, User user) throws IOException {

        String path = exchange.getRequestURI().getPath();
        Long id = Long.parseLong(path.substring("/tickets/".length()));

        ticketController.deleteTicket(id, user);
        exchange.sendResponseHeaders(204, -1);
    }

    private void handleGetAllTicketsIncludingDeleted(
            HttpExchange exchange, User user) throws IOException {

        try {
            List<TicketDTO> tickets =
                    ticketController.findAllIncludingDeleted(user);

            sendJson(exchange, 200, JsonUtil.toJson(tickets));

        } catch (BadRequestExeption e) {
            sendJson(exchange, 403, "{\"error\":\"Not allowed\"}");
        }
    }

    private void handleGetTicketIncludingDeleted(
            HttpExchange exchange, User user) throws IOException {

        try {
            String path = exchange.getRequestURI().getPath();
            Long id = Long.parseLong(
                    path.substring("/admin/tickets/".length()));

            TicketDTO ticket =
                    ticketController.findTicketByIdIncludingDeleted(id, user);

            if (ticket == null) {
                sendJson(exchange, 404,
                        "{\"error\":\"Ticket not found\"}");
                return;
            }

            sendJson(exchange, 200, JsonUtil.toJson(ticket));

        } catch (BadRequestExeption e) {
            sendJson(exchange, 403, "{\"error\":\"Not allowed\"}");
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, "{\"error\":\"Invalid ticket ID\"}");
        }
    }

    private void handleGetActiveTickets(
            HttpExchange exchange, User user) throws IOException {

        try {
            List<TicketDTO> tickets =
                    ticketController.findAllActive(user);

            sendJson(exchange, 200, JsonUtil.toJson(tickets));

        } catch (BadRequestExeption e) {
            sendJson(exchange, 403, "{\"error\":\"Not allowed\"}");
        }
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
