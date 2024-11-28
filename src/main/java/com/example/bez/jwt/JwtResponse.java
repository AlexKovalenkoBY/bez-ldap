package com.example.bez.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private String type = "Bearer";
    private String role;
    private String accessToken;
    private String refreshToken;


}

