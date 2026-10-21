package com.erdal.helpdeskpro.web.response;

import java.time.LocalDateTime;

import com.erdal.helpdeskpro.enums.Role;

public class LoginResponse {

    private final String token;
    private final Long userId;
    private final String username;
    private final String email;
    private final Role role;
    private final boolean active;
    private final LocalDateTime createdAt;

    public LoginResponse(
            String token,
            Long userId,
            String username,
            String email,
            Role role,
            boolean active,
            LocalDateTime createdAt) {

        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}