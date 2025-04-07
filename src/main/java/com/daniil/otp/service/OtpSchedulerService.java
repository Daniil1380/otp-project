package com.daniil.otp.service;

import com.daniil.otp.dao.OtpCodeRepository;
import com.daniil.otp.entity.OtpCode;
import com.daniil.otp.entity.OtpState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpSchedulerService {

    private final OtpCodeRepository otpCodeRepository;

    // Запуск каждую минуту
    @Scheduled(fixedRate = 60000)
    public void blockExpiredOtpCodes() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<OtpCode> expiredCodes = otpCodeRepository.findByCreatedAtBeforeAndState(oneHourAgo, OtpState.ACTIVE);

        if (expiredCodes.isEmpty()) {
            log.info("Нет просроченных OTP кодов для блокировки");
            return;
        }

        expiredCodes.forEach(code -> {
            code.setState(OtpState.EXPIRED);
            log.info("Блокировка кода {} (id: {})", code.getCode(), code.getId());
        });

        otpCodeRepository.saveAll(expiredCodes);
        log.info("Заблокировано {} OTP кодов", expiredCodes.size());
    }
}
