package com.erdal.helpdeskpro.web.response;

public class LoginResponse {

    // Kimliği doğrulanmış kullanıcının Bearer token'ı.
    private final String token;

    // Token'ın hangi kullanıcıya ait olduğunu gösteren kullanıcı ID'si.
    private final Long userId;

    // İstemciye gösterilebilecek kullanıcı adı.
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