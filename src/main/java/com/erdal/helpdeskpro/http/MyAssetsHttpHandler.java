package com.erdal.helpdeskpro.http;

import java.io.IOException;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.BaseAuthHandler;
import com.erdal.helpdeskpro.controller.MyAssetsController;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.config.util.JsonUtil;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MyAssetsHttpHandler extends BaseAuthHandler
        implements HttpHandler {

    private final MyAssetsController myAssetsController;

    public MyAssetsHttpHandler(
            MyAssetsController myAssetsController,
            AuthService authService) {

        super(authService);
        this.myAssetsController = myAssetsController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {

            JsonUtil.sendJson(
                    exchange,
                    405,
                    "{\"message\":\"Method not allowed\"}"
            );

            return;
        }

        User authenticatedUser;

        try {

            authenticatedUser = getAuthenticatedUser(exchange);

        } catch (SecurityException e) {

            JsonUtil.sendJson(
                    exchange,
                    401,
                    "{\"message\":\"Unauthorized: Invalid or missing token\"}"
            );

            return;
        }

        try {

            Long userId = authenticatedUser.getId();

            var assets = myAssetsController.getMyAssets(userId);

            JsonUtil.sendJson(
                    exchange,
                    200,
                    JsonUtil.toJson(assets)
            );

        } catch (Exception e) {

            e.printStackTrace();

            JsonUtil.sendJson(
                    exchange,
                    500,
                    "{\"message\":\"Failed to retrieve your assets\"}"
            );
        }
    }
}