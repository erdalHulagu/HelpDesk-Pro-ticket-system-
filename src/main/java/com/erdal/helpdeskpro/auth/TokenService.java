
package com.erdal.helpdeskpro.auth;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.IllegalArgumentException;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {

    // Güvenli ve tahmin edilmesi zor token üretir.
	
    private final SecureRandom secureRandom = new SecureRandom();

    // Şimdilik tokenları bellekte tutar.
    
    private final ConcurrentHashMap<String, User> activeTokens =
            new ConcurrentHashMap<>();

    public String generateToken() {
        byte[] randomBytes = new byte[32];

        secureRandom.nextBytes(randomBytes);

        // URL içinde kullanılabilecek 256 bit token.
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public String createToken(User user) {
        if (user == null) {
            throw new IllegalArgumentException(ExceptionMessage.USER_CANNOT_BE_NULL);
        }

        String token = generateToken();

        // Token ile kullanıcıyı eşleştir.
        activeTokens.put(token, user);

        return token;
    }

    public User getUserByToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        // Token kayıtlı değilse null döner.
        return activeTokens.get(token);
    }

    public void revokeToken(String token) {
        if (token != null) {
            // Çıkış yapıldığında tokenı iptal et.
            activeTokens.remove(token);
        }
    }
}
