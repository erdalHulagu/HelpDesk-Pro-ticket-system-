package com.erdal.helpdeskpro.auth;


import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.web.request.LoginRequest;
import com.erdal.helpdeskpro.web.request.RegisterRequest;
import com.erdal.helpdeskpro.web.response.LoginResponse;

public interface AuthService {

    // Yeni kullanıcı kaydı işlemini başlatır.
    void register(RegisterRequest request);

    // Kullanıcıyı doğrular ve başarılı girişte token döndürür.
    LoginResponse login(LoginRequest request);

    // Token üzerinden giriş yapan kullanıcıyı bulur.
    User authenticate(String token);

    // Mevcut oturumu sonlandırır.
    void logout(String token);
}