package com.erdal.helpdeskpro.auth;

import com.erdal.helpdeskpro.domain.User;
import com.sun.net.httpserver.HttpExchange;

public abstract class BaseAuthHandler {

    protected final AuthService authService;

    protected BaseAuthHandler(AuthService authService) {
        this.authService = authService;
    }

    protected User getAuthenticatedUser(HttpExchange exchange) {
        String authorizationHeader =
                exchange.getRequestHeaders().getFirst("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid token");
        }

        String token = authorizationHeader.substring(7).trim();

        if (token.isEmpty()) {
            throw new SecurityException("Missing token");
        }

        User user = authService.authenticate(token);

        if (user == null) {
            throw new SecurityException("Invalid or expired token");
        }

        return user;
    }
}