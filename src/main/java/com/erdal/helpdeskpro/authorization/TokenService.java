package com.erdal.helpdeskpro.authorization;

import com.erdal.helpdeskpro.domain.User;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {

    // Güvenli rastgele token üretmek için kullanılır.
    private final SecureRandom secureRandom = new SecureRandom();

    // Token ile kullanıcıyı eşleştirir.
    // Key: token, Value: token sahibi kullanıcı.
    private final ConcurrentHashMap<String, User> activeTokens =
            new ConcurrentHashMap<>();

    public String generateToken() {

        // 32 byte = 256 bit rastgele veri oluşturur.
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        // Rastgele veriyi URL ile uyumlu bir metne dönüştürür.
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public String createToken(User user) {

        // Kullanıcı olmadan token oluşturulmasını engeller.
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Yeni bir token üretir.
        String token = generateToken();

        // Token'ın hangi kullanıcıya ait olduğunu bellekte saklar.
        activeTokens.put(token, user);

        // İstemciye verilebilmesi için token'ı döndürür.
        return token;
    }

    public User getUserByToken(String token) {

        // Token boşsa kullanıcı bulunamaz.
        if (token == null || token.isBlank()) {
            return null;
        }

        // Token kayıtlıysa sahibini döndürür; değilse null döndürür.
        return activeTokens.get(token);
    }

    public void revokeToken(String token) {

        // Token'ı silerek bu token ile erişimi geçersiz kılar.
        if (token != null) {
            activeTokens.remove(token);
        }
    }
}
