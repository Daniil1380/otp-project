package com.daniil.otp.controller.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String operationId;
    private String deliveryType; // "EMAIL", "SMS", "FILE"
}
