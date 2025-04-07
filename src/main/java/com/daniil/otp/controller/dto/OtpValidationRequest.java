package com.daniil.otp.controller.dto;

import lombok.Data;

@Data
public class OtpValidationRequest {
    private String operationId;
    private String code;
}
