
package com.erdal.helpdeskpro.auth;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.domain.request.LoginRequest;
import com.erdal.helpdeskpro.domain.request.RegisterRequest;
import com.erdal.helpdeskpro.domain.response.LoginResponse;
import com.erdal.helpdeskpro.mapper.UserMapper;
import com.erdal.helpdeskpro.service.SecurityLogService;
import com.erdal.helpdeskpro.service.UserService;

public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final TokenService tokenService;
    private final SecurityLogService securityLogService;

    public AuthServiceImpl(
            UserService userService,
            TokenService tokenService,
            SecurityLogService securityLogService) {

        this.userService = userService;
        this.tokenService = tokenService;
        this.securityLogService = securityLogService;
    }

    @Override
    public void register(RegisterRequest registerRequest) {

        User user = UserMapper.registerRequestToUser(registerRequest);

        userService.registerUser(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return login(request, null);
    }

    @Override
    public LoginResponse login(LoginRequest request, String ipAddress) {

        if (request == null
                || request.getEmail() == null
                || request.getEmail().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Email and password are required.");
        }

        String email = request.getEmail().trim();

        User user;

        try {
            user = userService.login(email, request.getPassword());
        } catch (RuntimeException e) {
            safeLog(
                    null,
                    email,
                    "LOGIN_FAILED",
                    ipAddress,
                    "Login failed: " + e.getMessage()
            );

            throw e;
        }

        String token = tokenService.createToken(user);

        safeLog(
                user.getId(),
                user.getUsername(),
                "LOGIN_SUCCESS",
                ipAddress,
                "Successful login"
        );

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }

    @Override
    public User authenticate(String token) {
        return tokenService.getUserByToken(token);
    }

    @Override
    public void logout(String token) {
        logout(token, null);
    }

    @Override
    public void logout(String token, String ipAddress) {

        User user = authenticate(token);

        tokenService.revokeToken(token);

        safeLog(
                user.getId(),
                user.getUsername(),
                "LOGOUT",
                ipAddress,
                "User logged out"
        );
    }

    private void safeLog(
            Long userId,
            String username,
            String action,
            String ipAddress,
            String details) {

        try {
            securityLogService.log(
                    userId,
                    username,
                    action,
                    ipAddress,
                    details
            );

            System.out.println("SECURITY LOG SAVED: " + action);

        } catch (RuntimeException e) {
            System.err.println("SECURITY LOG ERROR:");
            e.printStackTrace();
        }
    }
}
