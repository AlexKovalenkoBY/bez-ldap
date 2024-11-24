package com.example.bez;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String role;

    public JwtResponse(String accessToken, String role) {
        this.token = accessToken;
        this.role = role;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public String getRole() {
        return role;
    }
}