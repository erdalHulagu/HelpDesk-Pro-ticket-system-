
package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.TicketActivityController;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.TicketActivityDTO;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TicketActivityHttpHandler implements HttpHandler {

    private final TicketActivityController ticketActivityController;
    private final AuthService authService;

    public TicketActivityHttpHandler(
            TicketActivityController ticketActivityController,
            AuthService authService) {

        this.ticketActivityController = ticketActivityController;
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

            // GET /activities/ticket/{ticketId}
            if ("GET".equalsIgnoreCase(method)
                    && path.matches("/activities/ticket/\\d+")) {

                handleGetActivitiesByTicket(exchange, authenticatedUser);
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

        	e.printStackTrace();
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

    private void handleGetActivitiesByTicket(
            HttpExchange exchange,
            User user) throws IOException {

        String path = exchange.getRequestURI().getPath();

        Long ticketId = Long.parseLong(
                path.substring("/activities/ticket/".length()));

        List<TicketActivityDTO> activities =
                ticketActivityController.getActivitiesByTicketId(
                        ticketId,
                        user
                );

        JsonUtil.sendJson(
                exchange,
                200,
                JsonUtil.toJson(activities)
        );
    }
}
