package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.erdal.helpdeskpro.controller.TicketController;
import com.erdal.helpdeskpro.dtos.TicketDTO;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handles all HTTP requests related to tickets.
 * Works like a REST controller but without Spring.
 */
public class TicketHttpHandler implements HttpHandler {

    private TicketController ticketController;

    public TicketHttpHandler() {
        // Normally you would inject dependencies here
        this.ticketController = DependencyContainer.getTicketController();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        /**
         * Route request based on HTTP method
         */
        switch (method) {

            case "GET":
                handleGetTickets(exchange);
                break;

            case "POST":
                handleCreateTicket(exchange);
                break;

            case "PUT":
                handleUpdateStatus(exchange);
                break;

            case "DELETE":
                handleDeleteTicket(exchange);
                break;

            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    /**
     * GET /tickets
     */
    private void handleGetTickets(HttpExchange exchange) throws IOException {

        // In real app user would come from auth token
        var user = AuthContext.getCurrentUser();

        var tickets = ticketController.getAllTickets(user);

        String json = JsonUtil.toJson(tickets);

        sendResponse(exchange, 200, json);
    }

    /**
     * POST /tickets
     */
    private void handleCreateTicket(HttpExchange exchange) throws IOException {

        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        TicketDTO dto = JsonUtil.fromJson(body, TicketDTO.class);

        var user = AuthContext.getCurrentUser();

        ticketController.creaetTicket(dto, user);

        sendResponse(exchange, 201, "Ticket created");
    }

    /**
     * PUT /tickets/status
     */
    private void handleUpdateStatus(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();
        Long ticketId = Long.parseLong(query.split("=")[1]);

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        TicketStatus status = TicketStatus.valueOf(body);

        var user = AuthContext.getCurrentUser();

        ticketController.uspDateTicetsStatus(ticketId, status, user);

        sendResponse(exchange, 200, "Status updated");
    }

    /**
     * DELETE /tickets?id=1
     */
    private void handleDeleteTicket(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();
        Long ticketId = Long.parseLong(query.split("=")[1]);

        var user = AuthContext.getCurrentUser();

        ticketController.deleteTicket(ticketId, user);

        sendResponse(exchange, 200, "Ticket deleted");
    }

    /**
     * Utility method to send HTTP response
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
//Bu Handler ne yapıyor?
//✔ HTTP method okuyor
//✔ Body parse ediyor
//✔ Controller çağırıyor
//✔ JSON response dönüyor
//Yani tam olarak:
//Spring’deki @RestController davranışı
