package com.example.library_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private final String token;
    private final String tokenType = "Bearer";
    private final String username;
    private final String role;
    private final Long memberId;

    public AuthResponse(String token, String username, String role, Long memberId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.memberId = memberId;
    }

    // Getters only (immutable DTO)
    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Long getMemberId() {
        return memberId;
    }
}