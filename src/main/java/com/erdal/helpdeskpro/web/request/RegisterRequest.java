
package com.erdal.helpdeskpro.web.request;

public class RegisterRequest {

    // Yeni kullanıcının görünen kullanıcı adı.
    private String username;

    // Yeni kullanıcının e-posta adresi.
    private String email;

    // Kayıt sırasında gönderilen düz metin şifre.
    // Veritabanına kaydetmeden önce BCrypt ile hash'lenmelidir.
    private String password;
    

    
    public RegisterRequest() {
        // JSON verilerinin nesneye dönüştürülmesini kolaylaştırır.
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
