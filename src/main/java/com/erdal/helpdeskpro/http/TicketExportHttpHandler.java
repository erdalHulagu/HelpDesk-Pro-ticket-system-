
package com.erdal.helpdeskpro.http;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.service.ExcelExportService;
import com.erdal.helpdeskpro.service.TicketService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class TicketExportHttpHandler implements HttpHandler {

    private final ExcelExportService excelExportService;
    private final TicketService ticketService;
    private final AuthService authService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TicketExportHttpHandler(
            ExcelExportService excelExportService,
            TicketService ticketService,
            AuthService authService) {

        this.excelExportService = excelExportService;
        this.ticketService = ticketService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            String authHeader = exchange.getRequestHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendError(exchange, 401, "Unauthorized");
                return;
            }

            String token = authHeader.substring(7);

            User currentUser = authService.authenticate(token);

            if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
                sendError(exchange, 403, "Access denied");
                return;
            }

            List<Ticket> tickets =
                    ticketService.getAllTicketsForAdmin(currentUser);

            List<String> headers = Arrays.asList(
                    "ID",
                    "Title",
                    "Description",
                    "Category",
                    "Status",
                    "Priority",
                    "Created By",
                    "Assigned To",
                    "Created At",
                    "SLA Due At",
                    "Resolved At",
                    "Deleted"
            );

            List<List<String>> data = new ArrayList<>();

            for (Ticket ticket : tickets) {

                String createdBy = ticket.getCreatedBy() != null
                        ? ticket.getCreatedBy().getUsername()
                        : "";

                String assignedTo = ticket.getAssignedTo() != null
                        ? ticket.getAssignedTo().getUsername()
                        : "Unassigned";

                List<String> row = Arrays.asList(
                        String.valueOf(ticket.getId()),
                        safeString(ticket.getTitle()),
                        safeString(ticket.getDescription()),
                        ticket.getCategory() != null
                                ? ticket.getCategory().name() : "",
                        ticket.getStatus() != null
                                ? ticket.getStatus().name() : "",
                        ticket.getPriority() != null
                                ? ticket.getPriority().name() : "",
                        safeString(createdBy),
                        safeString(assignedTo),
                        formatDate(ticket.getCreatedAt()),
                        formatDate(ticket.getSlaDueAt()),
                        formatDate(ticket.getResolvedAt()),
                        ticket.isDeleted() ? "Yes" : "No"
                );

                data.add(row);
            }

            byte[] excelFile = excelExportService.exportToExcel(
                    "Tickets",
                    headers,
                    data
            );

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );

            exchange.getResponseHeaders().set(
                    "Content-Disposition",
                    "attachment; filename=\"tickets.xlsx\""
            );

            exchange.getResponseHeaders().set("Cache-Control", "no-store");

            exchange.sendResponseHeaders(200, excelFile.length);

            try (var outputStream = exchange.getResponseBody()) {
                outputStream.write(excelFile);
            }

        } catch (Exception e) {

            e.printStackTrace();

            if (exchange.getResponseCode() == -1) {
                sendError(exchange, 500, "Failed to export tickets");
            }
        }
    }

    private String safeString(String value) {
        return value != null ? value : "";
    }

    private String formatDate(java.time.LocalDateTime date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    private void sendError(
            HttpExchange exchange,
            int statusCode,
            String message) throws IOException {

        byte[] response = message.getBytes("UTF-8");

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, response.length);

        try (var outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}
