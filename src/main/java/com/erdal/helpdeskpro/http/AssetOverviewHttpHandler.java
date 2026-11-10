package com.erdal.helpdeskpro.http;

import java.io.IOException;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.BaseAuthHandler;
import com.erdal.helpdeskpro.controller.AssetOverviewController;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.domain.dtos.AssetOverviewDTO;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.config.util.JsonUtil;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class AssetOverviewHttpHandler extends BaseAuthHandler implements HttpHandler {

	private final AssetOverviewController assetOverviewController;

	public AssetOverviewHttpHandler(AssetOverviewController assetOverviewController, AuthService authService) {

		super(authService);
		this.assetOverviewController = assetOverviewController;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			JsonUtil.sendJson(exchange, 405, "{\"message\":\"Method not allowed\"}");
			return;
		}

		User authenticatedUser;

		try {
			authenticatedUser = getAuthenticatedUser(exchange);

		} catch (SecurityException e) {

			JsonUtil.sendJson(exchange, 401, "{\"message\":\"Unauthorized: Invalid or missing token\"}");
			return;
		}

		Role role = authenticatedUser.getRole();

		if (role != Role.ADMIN && role != Role.IT_SUPPORT) {

			JsonUtil.sendJson(exchange, 403, "{\"message\":\"Access denied\"}");
			return;
		}

		try {

			AssetOverviewDTO overview = assetOverviewController.getOverview();

			JsonUtil.sendJson(exchange, 200, JsonUtil.toJson(overview));

		} catch (Exception e) {

			e.printStackTrace();

			JsonUtil.sendJson(exchange, 500, "{\"message\":\"Failed to retrieve asset overview\"}");
		}
	}
}