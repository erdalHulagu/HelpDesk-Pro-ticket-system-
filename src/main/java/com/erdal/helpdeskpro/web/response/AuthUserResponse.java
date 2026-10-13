package com.erdal.helpdeskpro.web.response;

import com.erdal.helpdeskpro.enums.Role;

public class AuthUserResponse {

    // Kullanıcının sistemdeki kimliği.
    private final Long id;

    // Kullanıcının görünen adı.
    private final String username;

    // Kullanıcının e-posta adresi.
    private final String email;

    // Yetkilendirmede kullanılacak rol.
    private final Role role;

    public AuthUserResponse(
            Long id,
            String username,
            String email,
            Role role) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
