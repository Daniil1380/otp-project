package com.daniil.otp.service;

import com.daniil.otp.controller.dto.OtpRequest;
import com.daniil.otp.controller.dto.OtpValidationRequest;
import com.daniil.otp.dao.OtpCodeRepository;
import com.daniil.otp.dao.OtpConfigRepository;
import com.daniil.otp.dao.UserRepository;
import com.daniil.otp.entity.OtpCode;
import com.daniil.otp.entity.OtpConfig;
import com.daniil.otp.entity.OtpState;
import com.daniil.otp.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final OtpConfigRepository otpConfigRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmppService smppService;
    private final UserService userService;
    private final TelegramService telegramService;

    private static final SecureRandom random = new SecureRandom();

    public String generateOtp(OtpRequest request) {
        OtpConfig config = otpConfigRepository.findTopByOrderByIdAsc();
        int length = config != null ? config.getOtpLength() : 6;
        String code = generateNumericCode(length);

        User currentUser = getCurrentUser();

        OtpCode otp = OtpCode.builder()
                .code(code)
                .operationId(request.getOperationId())
                .createdAt(LocalDateTime.now())
                .user(currentUser)
                .state(OtpState.ACTIVE)
                .build();

        otpCodeRepository.save(otp);

        // "Отправка" кода
        switch (request.getDeliveryType().toUpperCase()) {
            case "FILE" -> saveToFile(currentUser.getLogin(), code);
            case "EMAIL" -> emailService.sendEmail(currentUser, code);
            case "SMS" -> smppService.sendSms(currentUser.getPhoneNumber(), code);
            case "TELEGRAM" -> telegramService.sendCode(currentUser.getTelegram(), code);
            default -> throw new RuntimeException("Неподдерживаемый тип доставки");
        }

        return code;
    }

    public boolean validateOtp(OtpValidationRequest request) {
        Optional<OtpCode> otpOptional = otpCodeRepository.findByOperationId(request.getOperationId());
        if (otpOptional.isEmpty()) return false;

        if (otpOptional.get().getState() != OtpState.ACTIVE) return false;

        OtpCode otp = otpOptional.get();
        otp.setState(OtpState.USED);
        otpCodeRepository.save(otp);
        // Можно добавить проверку времени жизни
        return otp.getCode().equals(request.getCode());
    }

    private String generateNumericCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private void saveToFile(String username, String code) {
        try (FileWriter writer = new FileWriter("otp_" + username + ".txt")) {
            writer.write("OTP код: " + code);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи OTP в файл");
        }
    }

    private User getCurrentUser() {
        String login = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userService.getByLogin(login);
    }
}