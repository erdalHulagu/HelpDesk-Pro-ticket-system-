
package com.erdal.helpdeskpro.repository;

import com.erdal.helpdeskpro.domain.AuthToken;

public interface AuthTokenRepository {

    void save(AuthToken token);

    AuthToken findByTokenHash(String tokenHash);

    void revokeByTokenHash(String tokenHash);
}
