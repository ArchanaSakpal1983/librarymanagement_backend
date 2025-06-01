
// AuthResponse.java
// AuthRequest / AuthResponse DTOs – Manage login input/output

package com.example.library_management.dto;

public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}