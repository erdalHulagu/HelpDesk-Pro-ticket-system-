package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.TicketController;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.TicketDTO;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TicketHttpHandler implements HttpHandler {

	private final TicketController ticketController;
	private final AuthService authService;

	public TicketHttpHandler(TicketController ticketController, AuthService authService) {
		this.ticketController = ticketController;
		this.authService = authService;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		try {
			User authenticatedUser = authenticateRequest(exchange);

			if (authenticatedUser == null) {
				JsonUtil.sendJson(exchange, 401, "{\"error\":\"Unauthorized\"}");
				return;
			}

			String path = exchange.getRequestURI().getPath();
			String method = exchange.getRequestMethod();

			if ("POST".equalsIgnoreCase(method) && path.equals("/tickets")) {
				handleCreateTicket(exchange, authenticatedUser);
				return;
			}

			if ("GET".equalsIgnoreCase(method) && path.equals("/tickets")) {
				handleGetAllTickets(exchange, authenticatedUser);
				return;
			}

			if ("GET".equalsIgnoreCase(method) && path.equals("/tickets/user")) {
				handleGetUsersTickets(exchange, authenticatedUser);
				return;
			}

			if ("GET".equalsIgnoreCase(method) && path.equals("/tickets/active")) {
				handleGetActiveTickets(exchange, authenticatedUser);
				return;
			}

			if ("GET".equalsIgnoreCase(method) && path.matches("/tickets/\\d+")) {
				handleGetTicketById(exchange, authenticatedUser);
				return;
			}

			if ("PUT".equalsIgnoreCase(method) && path.matches("/tickets/\\d+/status")) {
				handleUpdateStatus(exchange, authenticatedUser);
				return;
			}

			if ("PUT".equalsIgnoreCase(method) && path.matches("/tickets/\\d+/assign")) {
				handleAssignTicket(exchange, authenticatedUser);
				return;
			}

			if ("DELETE".equalsIgnoreCase(method) && path.matches("/tickets/\\d+")) {
				handleDeleteTicket(exchange, authenticatedUser);
				return;
			}

			if ("GET".equalsIgnoreCase(method) && path.equals("/admin/tickets")) {
				handleGetAllTicketsIncludingDeleted(exchange, authenticatedUser);
				return;
			}

			if ("GET".equalsIgnoreCase(method) && path.matches("/admin/tickets/\\d+")) {
				handleGetTicketIncludingDeleted(exchange, authenticatedUser);
				return;
			}

			JsonUtil.sendJson(exchange, 404, "{\"error\":\"Endpoint not found\"}");

		} catch (ResourceNotFoundExeption e) {
			JsonUtil.sendJson(exchange, 404, "{\"error\":\"Ticket not found\"}");
//
//        } catch (BadRequestExeption e) {
//            sendJson(exchange, 403,
//                    "{\"error\":\"Not allowed\"}");
		} catch (BadRequestExeption e) {
			e.printStackTrace();

			JsonUtil.sendJson(exchange, 403, JsonUtil.toJson(java.util.Map.of("error", e.getMessage())));

		} catch (JsonProcessingException e) {
			JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid JSON\"}");

		} catch (IllegalArgumentException e) {
			JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid request\"}");

		} catch (Exception e) {
			e.printStackTrace();

			try {
				JsonUtil.sendJson(exchange, 500, "{\"error\":\"Internal Server Error\"}");
			} catch (IOException ignored) {
				// Yanıt zaten gönderilmiş veya bağlantı kapanmış olabilir.
			}
		} finally {
			exchange.close();
		}
	}

	private User authenticateRequest(HttpExchange exchange) {

		String header = exchange.getRequestHeaders().getFirst("Authorization");

		if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
			return null;
		}

		String token = header.substring(7).trim();

		if (token.isEmpty()) {
			return null;
		}

		return authService.authenticate(token);
	}

	private void handleCreateTicket(HttpExchange exchange, User user) throws IOException {

		String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

		TicketDTO request = JsonUtil.fromJson(body, TicketDTO.class);
		TicketDTO created = ticketController.createTicket(request, user);

		JsonUtil.sendJson(exchange, 201, JsonUtil.toJson(created));
	}

	private void handleGetAllTickets(HttpExchange exchange, User user) throws IOException {

		List<TicketDTO> tickets = ticketController.findAllActive(user);

		JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(tickets));
	}

	private void handleGetUsersTickets(HttpExchange exchange, User user) throws IOException {

		List<TicketDTO> tickets = ticketController.getusersTickets(user);

		JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(tickets));
	}

	private void handleGetTicketById(HttpExchange exchange, User user) throws IOException {

		String path = exchange.getRequestURI().getPath();
		Long id = Long.parseLong(path.substring("/tickets/".length()));

		TicketDTO ticket = ticketController.getTicket(id, user);

		JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(ticket));
	}

	private void handleUpdateStatus(HttpExchange exchange, User user) throws IOException {

		String path = exchange.getRequestURI().getPath();
		String[] parts = path.split("/");

		if (parts.length != 4 || !"status".equals(parts[3])) {
			JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid ticket URL\"}");
			return;
		}

		Long id;

		try {
			id = Long.parseLong(parts[2]);
			if (id <= 0) {
				JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid ticket ID\"}");
				return;
			}
		} catch (NumberFormatException e) {
			JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid ticket ID\"}");
			return;
		}

		String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

		try {
			TicketDTO request = JsonUtil.fromJson(body, TicketDTO.class);

			if (request == null || request.getStatus() == null) {
				JsonUtil.sendJson(exchange, 400, "{\"error\":\"Status is required\"}");
				return;
			}

			ticketController.updateTicketStatus(id, request.getStatus(), user);

			exchange.sendResponseHeaders(204, -1);

		} catch (IllegalArgumentException e) {
			JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid status or request\"}");
		}
	}

	private void handleDeleteTicket(HttpExchange exchange, User user) throws IOException {

		String path = exchange.getRequestURI().getPath();
		Long id = Long.parseLong(path.substring("/tickets/".length()));

		ticketController.deleteTicket(id, user);

		exchange.sendResponseHeaders(204, -1);
	}

	private void handleGetAllTicketsIncludingDeleted(HttpExchange exchange, User user) throws IOException {

		List<TicketDTO> tickets = ticketController.findAllIncludingDeleted(user);

		JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(tickets));
	}

	private void handleGetTicketIncludingDeleted(HttpExchange exchange, User user) throws IOException {

		String path = exchange.getRequestURI().getPath();
		Long id = Long.parseLong(path.substring("/admin/tickets/".length()));

		TicketDTO ticket = ticketController.findTicketByIdIncludingDeleted(id, user);

		if (ticket == null) {
			JsonUtil.sendJson(exchange, 404, "{\"error\":\"Ticket not found\"}");
			return;
		}

		JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(ticket));
	}

	private void handleGetActiveTickets(HttpExchange exchange, User user) throws IOException {

		List<TicketDTO> tickets = ticketController.findAllActive(user);

		JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(tickets));
	}

	private void handleAssignTicket(HttpExchange exchange, User user) throws IOException {

		String path = exchange.getRequestURI().getPath();
		String[] parts = path.split("/");

		if (parts.length != 4 || !"assign".equals(parts[3])) {
			JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid ticket URL\"}");
			return;
		}

		Long ticketId;

		try {
			ticketId = Long.parseLong(parts[2]);

			if (ticketId <= 0) {
				JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid ticket ID\"}");
				return;
			}
		} catch (NumberFormatException e) {
			JsonUtil.sendJson(exchange, 400, "{\"error\":\"Invalid ticket ID\"}");
			return;
		}

		String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

		TicketDTO request = JsonUtil.fromJson(body, TicketDTO.class);

		if (request == null || request.getAssignedToId() == null) {
			JsonUtil.sendJson(exchange, 400, "{\"error\":\"Assigned user ID is required\"}");
			return;
		}

		ticketController.assignTicket(ticketId, request.getAssignedToId(), user);

		exchange.sendResponseHeaders(204, -1);
	}

}
