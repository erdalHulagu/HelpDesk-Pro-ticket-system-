
package com.erdal.helpdeskpro.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import com.erdal.helpdeskpro.domain.AuthToken;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.IllegalArgumentException;
import com.erdal.helpdeskpro.repository.AuthTokenRepository;
import com.erdal.helpdeskpro.repository.UserRepository;

public class TokenService {

    private static final long TOKEN_VALIDITY_HOURS = 8;

    private final SecureRandom secureRandom = new SecureRandom();
    private final AuthTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public TokenService(
            AuthTokenRepository tokenRepository,
            UserRepository userRepository) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public String createToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException(
                    ExceptionMessage.USER_CANNOT_BE_NULL
            );
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    ExceptionMessage.USER_NOT_ACTIVE
            );
        }

        String rawToken = generateToken();
        LocalDateTime now = LocalDateTime.now();

        AuthToken token = new AuthToken();
        token.setTokenHash(hashToken(rawToken));
        token.setUser(user);
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusHours(TOKEN_VALIDITY_HOURS));
        token.setRevoked(false);

        tokenRepository.save(token);

        // Ham token yalnızca istemciye döndürülür.
        return rawToken;
    }

    public User getUserByToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return null;
        }

        AuthToken token = tokenRepository.findByTokenHash(
                hashToken(rawToken)
        );

        if (token == null || token.isRevoked()) {
            return null;
        }

        if (token.getExpiresAt() == null
                || !token.getExpiresAt().isAfter(LocalDateTime.now())) {

            tokenRepository.revokeByTokenHash(token.getTokenHash());
            return null;
        }

        Long userId = token.getUser().getId();

        // Kullanıcının aktiflik durumunu veritabanından yeniden kontrol et.
        User currentUser = userRepository.findById(userId);

        if (currentUser == null || !currentUser.isActive()) {
            tokenRepository.revokeByTokenHash(token.getTokenHash());
            return null;
        }

        return currentUser;
    }

    public void revokeToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        tokenRepository.revokeByTokenHash(hashToken(rawToken));
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 is not available",
                    e
            );
        }
    }
}
