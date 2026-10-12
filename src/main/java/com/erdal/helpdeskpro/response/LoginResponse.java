package com.erdal.helpdeskpro.response;

public class LoginResponse {

    // Kullanıcının sonraki isteklerde göndereceği token.
    private final String token;

    // Giriş yapan kullanıcının kimliği.
    private final Long userId;

    // Kullanıcıya ait görünen ad.
    private final String username;

    public LoginResponse(String token, Long userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
