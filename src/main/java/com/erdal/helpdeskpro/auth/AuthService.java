package com.erdal.helpdeskpro.auth;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.web.request.LoginRequest;
import com.erdal.helpdeskpro.web.request.RegisterRequest;
import com.erdal.helpdeskpro.web.response.LoginResponse;

public interface AuthService {

    void register(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest request);

    LoginResponse login(LoginRequest request, String ipAddress);

    User authenticate(String token);

    void logout(String token);

    void logout(String token, String ipAddress);
}
