package com.example.bez;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsResponse {
    private String username;
    private String commonName;
    private String surname;
    private String givenName;
    private String displayName;
    private String role;

    public UserDetailsResponse(String username, String commonName, String surname, String givenName, String displayName, String role) {
        this.username = username;
        this.commonName = commonName;
        this.surname = surname;
        this.givenName = givenName;
        this.displayName = displayName;
        this.role = role;
    }


}