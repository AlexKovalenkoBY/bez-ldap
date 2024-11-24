package com.example.bez;

public class JwtResponse {
    private String token;
    private String type = "Bearer";

    public JwtResponse(String accessToken) {
        this.token = accessToken;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
}