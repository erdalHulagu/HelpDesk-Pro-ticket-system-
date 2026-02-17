package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.controller.TicketController;
import com.erdal.helpdeskpro.dtos.TicketDTO;
import com.erdal.helpdeskpro.domain.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * HTTP Handler for Ticket REST API.
 * Supports GET, POST, PUT, DELETE
 */
public class TicketHttpHandler implements HttpHandler {

    private final TicketController ticketController;
    private final User authenticatedUser; // In real app, get this from auth

    public TicketHttpHandler(TicketController ticketController, User authenticatedUser) {
        this.ticketController = ticketController;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            // ---------------- POST /tickets ----------------
            if ("POST".equalsIgnoreCase(method) && path.equals("/tickets")) {
                handleCreateTicket(exchange);
                return;
            }

            // ---------------- GET /tickets ----------------
            if ("GET".equalsIgnoreCase(method) && path.equals("/tickets")) {
                handleGetAllTickets(exchange);
                return;
            }

            // ---------------- GET /tickets/{id} ----------------
            if ("GET".equalsIgnoreCase(method) && path.matches("/tickets/\\d+")) {
                handleGetTicketById(exchange);
                return;
            }

            // ---------------- PUT /tickets/{id}/status ----------------
            if ("PUT".equalsIgnoreCase(method) && path.matches("/tickets/\\d+/status")) {
                handleUpdateStatus(exchange);
                return;
            }

            // ---------------- DELETE /tickets/{id} ----------------
            if ("DELETE".equalsIgnoreCase(method) && path.matches("/tickets/\\d+")) {
                handleDeleteTicket(exchange);
                return;
            }

            // 404 for unknown paths
            exchange.sendResponseHeaders(404, -1);

        } catch (Exception e) {
            // Exception handling: return JSON error
            String errorJson = "{\"error\":\"" + e.getMessage() + "\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, errorJson.getBytes().length);
            exchange.getResponseBody().write(errorJson.getBytes());
            exchange.close();
        }
    }

    // ---------------- Helper methods ----------------

    private void handleCreateTicket(HttpExchange exchange) throws Exception {
        InputStream is = exchange.getRequestBody();
        TicketDTO ticketDTO = JsonUtil.fromJson(new String(is.readAllBytes()), TicketDTO.class);

        ticketController.createTicket(ticketDTO, authenticatedUser);

        String response = JsonUtil.toJson(ticketDTO);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(201, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleGetAllTickets(HttpExchange exchange) throws Exception {
        List<TicketDTO> tickets = ticketController.getAllTicketsForAdmin(authenticatedUser);

        String response = JsonUtil.toJson(tickets);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleGetTicketById(HttpExchange exchange) throws Exception {
        String path = exchange.getRequestURI().getPath();
        Long ticketId = Long.parseLong(path.substring("/tickets/".length()));

        TicketDTO ticketDTO = ticketController.getTicket(ticketId, authenticatedUser);

        String response = JsonUtil.toJson(ticketDTO);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void handleUpdateStatus(HttpExchange exchange) throws Exception {
        String path = exchange.getRequestURI().getPath();
        Long ticketId = Long.parseLong(path.split("/")[2]);

        TicketDTO request = JsonUtil.fromJson(new String(exchange.getRequestBody().readAllBytes()), TicketDTO.class);
        ticketController.uspDateTicetsStatus(ticketId, request.getStatus(), authenticatedUser);

        exchange.sendResponseHeaders(204, -1); // No content
        exchange.close();
    }

    private void handleDeleteTicket(HttpExchange exchange) throws Exception {
        String path = exchange.getRequestURI().getPath();
        Long ticketId = Long.parseLong(path.substring("/tickets/".length()));

        ticketController.deleteTicket(ticketId, authenticatedUser);

        exchange.sendResponseHeaders(204, -1); // No content
        exchange.close();
    }
}
