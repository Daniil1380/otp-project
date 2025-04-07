package com.daniil.otp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "otp_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_length", nullable = false)
    private Integer otpLength;

    // Можно добавить другие параметры конфигурации OTP
}
