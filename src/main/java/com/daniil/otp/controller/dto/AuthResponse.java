package com.daniil.otp.controller.dto;

public class AuthResponse {
    public String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
