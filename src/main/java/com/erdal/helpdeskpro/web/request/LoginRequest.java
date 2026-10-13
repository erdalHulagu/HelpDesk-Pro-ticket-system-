package com.erdal.helpdeskpro.web.request;

public class LoginRequest {

    // Kullanıcının giriş yaparken gönderdiği e-posta.
    private String email;

    // Kullanıcının giriş yaparken gönderdiği şifre.
    private String password;

    public LoginRequest() {
        // JSON verilerinin nesneye dönüştürülmesini kolaylaştırır.
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

