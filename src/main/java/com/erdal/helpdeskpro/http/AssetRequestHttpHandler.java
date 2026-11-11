
package com.erdal.helpdeskpro.http;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.BaseAuthHandler;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.erdal.helpdeskpro.controller.AssetRequestController;
import com.erdal.helpdeskpro.domain.request.AssetRequest;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.AssetRequestStatus;
import com.erdal.helpdeskpro.enums.Role;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import com.erdal.helpdeskpro.domain.response.AssetRequestResponse;
import java.util.stream.Collectors;
import com.erdal.helpdeskpro.service.NotificationService;

public class AssetRequestHttpHandler extends BaseAuthHandler implements HttpHandler {

	private final AssetRequestController requestController;
	private final NotificationService notificationService;
	

	public AssetRequestHttpHandler(AssetRequestController requestController, AuthService authService,NotificationService notificationService) {
		super(authService);
		this.requestController = requestController;
		this.notificationService = notificationService;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		try {
			User authenticatedUser = getAuthenticatedUser(exchange);

			if (authenticatedUser == null) {
				sendJson(exchange, 401, "{\"error\":\"Unauthorized\"}");
				return;
			}

			String method = exchange.getRequestMethod();
			String path = exchange.getRequestURI().getPath();

			// GET /asset-requests
			if ("GET".equalsIgnoreCase(method) && "/asset-requests".equals(path)) {

				if (!isAdminOrITSupport(authenticatedUser)) {
					sendJson(exchange, 403, "{\"error\":\"Access denied\"}");
					return;
				}

				handleGetAll(exchange);
				return;
			}

			// POST /asset-requests
			if ("POST".equalsIgnoreCase(method) && "/asset-requests".equals(path)) {

				handleCreate(exchange, authenticatedUser);
				return;
			}

			// GET /asset-requests/my
			if ("GET".equalsIgnoreCase(method) && "/asset-requests/my".equals(path)) {

				handleGetMyRequests(exchange, authenticatedUser);
				return;
			}

			// GET /asset-requests/status/PENDING
			if (path.matches("/asset-requests/status/[A-Z_]+") && "GET".equalsIgnoreCase(method)) {

				if (!isAdminOrITSupport(authenticatedUser)) {
					sendJson(exchange, 403, "{\"error\":\"Access denied\"}");
					return;
				}

				String status = path.substring(path.lastIndexOf("/") + 1);

				handleGetByStatus(exchange, status);
				return;
			}

			
			// PUT /asset-requests/{id}/review
			if (path.matches("/asset-requests/\\d+/review") && "PUT".equalsIgnoreCase(method)) {

				if (!isAdminOrITSupport(authenticatedUser)) {
					sendJson(exchange, 403, "{\"error\":\"Access denied\"}");
					return;
				}

				handleReview(exchange, extractRequestId(path), authenticatedUser);
				return;
			}

			// PUT /asset-requests/{id}/cancel
			if (path.matches("/asset-requests/\\d+/cancel") && "PUT".equalsIgnoreCase(method)) {

				handleCancel(exchange, extractRequestId(path), authenticatedUser);
				return;
			}

			// PUT /asset-requests/{id}/fulfill
			if (path.matches("/asset-requests/\\d+/fulfill") && "PUT".equalsIgnoreCase(method)) {

				if (!isAdminOrITSupport(authenticatedUser)) {
					sendJson(exchange, 403, "{\"error\":\"Access denied\"}");
					return;
				}

				handleFulfill(exchange, extractRequestId(path));
				return;
			}

			// GET /asset-requests/{id}
			if (path.matches("/asset-requests/\\d+") && "GET".equalsIgnoreCase(method)) {

				handleGetById(exchange, extractRequestId(path), authenticatedUser);
				return;
			}

			sendJson(exchange, 404, "{\"error\":\"Endpoint not found\"}");

		} catch (IllegalArgumentException e) {

			sendJson(exchange, 400, JsonUtil.toJson(Map.of("error", e.getMessage())));

		} catch (Exception e) {

			e.printStackTrace();

			sendJson(exchange, 500, "{\"error\":\"Internal server error\"}");
		}
	}

	private boolean isAdminOrITSupport(User user) {

		return user.getRole() == Role.ADMIN || user.getRole() == Role.IT_SUPPORT;
	}

	private void handleCreate(HttpExchange exchange, User authenticatedUser) throws IOException {

		String body = readBody(exchange);

		AssetRequest request = JsonUtil.fromJson(body, AssetRequest.class);

		if (request == null || request.getCategory() == null || request.getDescription() == null
				|| request.getDescription().isBlank()) {

			sendJson(exchange, 400, "{\"error\":\"Category and description are required\"}");
			return;
		}

		// Kullanıcı bilgisi token üzerinden belirlenir.
		request.setUser(authenticatedUser);

		request.setId(null);
		request.setStatus(AssetRequestStatus.PENDING);
		request.setReviewedBy(null);
		request.setReviewedAt(null);
		request.setAsset(null);

		requestController.createRequest(request);

		sendJson(exchange, 201, "{\"message\":\"Asset request created successfully\"}");
	}

//	private void handleGetAll(HttpExchange exchange) throws IOException {
//
//		List<AssetRequest> requests = requestController.getAllRequests();
//
//		sendJson(exchange, 200, JsonUtil.toJson(requests));
//	}
	private void handleGetAll(HttpExchange exchange) throws IOException {
	    List<AssetRequestResponse> responses =
	            requestController.getAllRequests()
	                    .stream()
	                    .map(AssetRequestResponse::fromEntity)
	                    .collect(Collectors.toList());

	    sendJson(exchange, 200, JsonUtil.toJson(responses));
	}
	private void handleGetMyRequests(
	        HttpExchange exchange,
	        User authenticatedUser
	) throws IOException {

	    List<AssetRequestResponse> responses =
	            requestController.getRequestsByUserId(authenticatedUser.getId())
	                    .stream()
	                    .map(AssetRequestResponse::fromEntity)
	                    .collect(Collectors.toList());

	    sendJson(exchange, 200, JsonUtil.toJson(responses));
	}

	private void handleGetById(HttpExchange exchange, Long id, User authenticatedUser) throws IOException {

		AssetRequest request = requestController.getRequestById(id);

		if (request == null) {
			sendJson(exchange, 404, "{\"error\":\"Asset request not found\"}");
			return;
		}

		boolean adminOrIT = isAdminOrITSupport(authenticatedUser);

		boolean isOwner = request.getUser() != null && request.getUser().getId().equals(authenticatedUser.getId());

		if (!adminOrIT && !isOwner) {
			sendJson(exchange, 403, "{\"error\":\"You cannot access this request\"}");
			return;
		}

		sendJson(exchange, 200, JsonUtil.toJson(request));
	}

	private void handleGetByStatus(HttpExchange exchange, String statusValue) throws IOException {

		AssetRequestStatus status;

		try {
			status = AssetRequestStatus.valueOf(statusValue);
		} catch (IllegalArgumentException e) {
			sendJson(exchange, 400, "{\"error\":\"Invalid request status\"}");
			return;
		}

		List<AssetRequest> requests = requestController.getRequestsByStatus(status);

		sendJson(exchange, 200, JsonUtil.toJson(requests));
	}

	private void handleReview(
	        HttpExchange exchange,
	        Long requestId,
	        User authenticatedUser
	) throws IOException {

	    String body = readBody(exchange);
	    Map<?, ?> requestBody = JsonUtil.fromJson(body, Map.class);

	    if (requestBody == null || requestBody.get("status") == null) {
	        sendJson(exchange, 400,
	                "{\"error\":\"Status is required\"}");
	        return;
	    }

	    AssetRequestStatus status;

	    try {
	        status = AssetRequestStatus.valueOf(
	                requestBody.get("status").toString().toUpperCase()
	        );
	    } catch (IllegalArgumentException e) {
	        sendJson(exchange, 400,
	                "{\"error\":\"Invalid request status\"}");
	        return;
	    }

	    if (status != AssetRequestStatus.APPROVED &&
	            status != AssetRequestStatus.REJECTED) {

	        sendJson(exchange, 400,
	                "{\"error\":\"Only APPROVED or REJECTED are allowed\"}");
	        return;
	    }

	    // Talebi işlemden önce alıyoruz.
	    AssetRequest request = requestController.getRequestById(requestId);

	    if (request == null || request.getUser() == null) {
	        sendJson(exchange, 404,
	                "{\"error\":\"Asset request not found\"}");
	        return;
	    }

	    Long recipientId = request.getUser().getId();
	    String category = request.getCategory().name();

	    // Önce talep durumunu güncelle.
	    requestController.reviewRequest(
	            requestId,
	            status,
	            authenticatedUser.getId()
	    );

	    // Ardından talep sahibine bildirim gönder.
	    String title = status == AssetRequestStatus.APPROVED
	            ? "Equipment Request Approved"
	            : "Equipment Request Rejected";

	    String message = status == AssetRequestStatus.APPROVED
	            ? "Your " + category + " equipment request has been approved."
	            : "Your " + category + " equipment request has been rejected.";

	    notificationService.createNotification(
	            recipientId,
	            null,
	            authenticatedUser.getId(),
	            "ASSET_REQUEST",
	            title,
	            message
	    );

	    sendJson(exchange, 200,
	            "{\"message\":\"Asset request reviewed and notification sent successfully\"}");
	}

	private void handleCancel(HttpExchange exchange, Long requestId, User authenticatedUser) throws IOException {

		requestController.cancelRequest(requestId, authenticatedUser.getId());

		sendJson(exchange, 200, "{\"message\":\"Asset request cancelled successfully\"}");
	}

	private void handleFulfill(HttpExchange exchange, Long requestId) throws IOException {

		String body = readBody(exchange);

		Map<?, ?> requestBody = JsonUtil.fromJson(body, Map.class);

		if (requestBody == null || requestBody.get("assetId") == null) {
			sendJson(exchange, 400, "{\"error\":\"Asset ID is required\"}");
			return;
		}

		Long assetId = ((Number) requestBody.get("assetId")).longValue();

		requestController.fulfillRequest(requestId, assetId);

		sendJson(exchange, 200, "{\"message\":\"Asset request fulfilled successfully\"}");
	}

	private String readBody(HttpExchange exchange) throws IOException {

		return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
	}

	private Long extractRequestId(String path) {

		String[] parts = path.split("/");
		return Long.parseLong(parts[2]);
	}

	private void sendJson(HttpExchange exchange, int statusCode, String response) throws IOException {

		byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

		exchange.sendResponseHeaders(statusCode, bytes.length);

		exchange.getResponseBody().write(bytes);
		exchange.getResponseBody().close();
	}
}
