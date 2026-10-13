
package com.erdal.helpdeskpro.controller;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.web.request.LoginRequest;
import com.erdal.helpdeskpro.web.request.RegisterRequest;
import com.erdal.helpdeskpro.web.response.LoginResponse;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Yeni kullanıcı kaydı başlatır.
    public void register(RegisterRequest request) {
        authService.register(request);
    }

    // Giriş yapar ve token döndürür.
    public LoginResponse login(LoginRequest request) {
        return authService.login(request);
    }

    // Çıkış yaparak tokenı iptal eder.
    public void logout(String token) {
        authService.logout(token);
    }
}
