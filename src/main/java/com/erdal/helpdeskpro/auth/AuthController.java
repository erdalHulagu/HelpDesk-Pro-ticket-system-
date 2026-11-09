
package com.erdal.helpdeskpro.auth;

import com.erdal.helpdeskpro.domain.request.LoginRequest;
import com.erdal.helpdeskpro.domain.request.RegisterRequest;
import com.erdal.helpdeskpro.domain.response.LoginResponse;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void register(RegisterRequest request) {
        authService.register(request);
    }

    public LoginResponse login(LoginRequest request) {
        return authService.login(request);
    }

    public LoginResponse login(LoginRequest request, String ipAddress) {
        return authService.login(request, ipAddress);
    }

    public void logout(String token) {
        authService.logout(token);
    }

    public void logout(String token, String ipAddress) {
        authService.logout(token, ipAddress);
    }
}
