package com.daniil.otp.controller.dto;

public class RegisterRequest {
    public String login;
    public String password;
    public String role; // "ADMIN" или "USER"
    public String email;
    public String phoneNumber;
    public String telegram;
}
