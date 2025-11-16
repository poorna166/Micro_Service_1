package com.skinflex.auth_service.dto;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private Long expiresIn;
    private String username;
    private String email;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, String refreshToken, Long expiresIn, String username, String email, String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.username = username;
        this.email = email;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
