package com.daniil.otp.controller.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String login;
    private String password;
    private String role; // "ADMIN" или "USER"
    private String email;
    private String phoneNumber;
    private String telegram;
}
