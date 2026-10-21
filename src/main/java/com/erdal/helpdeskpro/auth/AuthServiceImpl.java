
package com.erdal.helpdeskpro.auth;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.service.UserService;
import com.erdal.helpdeskpro.web.request.LoginRequest;
import com.erdal.helpdeskpro.web.request.RegisterRequest;
import com.erdal.helpdeskpro.web.response.LoginResponse;

public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final TokenService tokenService;

    public AuthServiceImpl(
            UserService userService,
            TokenService tokenService) {

        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public void register(RegisterRequest request) {

        // Boş kayıt isteğini reddet.
        if (request == null
                || isBlank(request.getUsername())
                || isBlank(request.getEmail())
                || isBlank(request.getPassword())) {
            throw new IllegalArgumentException(
                    "Username, email and password are required");
        }

        // Rolü ve aktiflik durumunu kullanıcıdan alma.
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim());
        user.setPassword(request.getPassword());

        // Mevcut servis şifreyi BCrypt ile hash'ler.
        userService.registerUser(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        if (request == null
                || isBlank(request.getEmail())
                || isBlank(request.getPassword())) {
            throw new IllegalArgumentException(
                    "Email and password are required");
        }

        // Mevcut servis kullanıcıyı ve şifreyi doğrular.
        User user = userService.login(
                request.getEmail().trim(),
                request.getPassword()
        );

        // Doğrulanan kullanıcı için token üret.
        String token = tokenService.createToken(user);

     // // Şifreyi response'a ekleme.
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
        // Token geçersizse veya bulunamazsa null dönebilir.
        return tokenService.getUserByToken(token);
    }

    @Override
    public void logout(String token) {
        // Mevcut token'ı geçersiz kıl.
        tokenService.revokeToken(token);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
