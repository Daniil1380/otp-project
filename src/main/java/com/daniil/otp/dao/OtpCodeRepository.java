package com.daniil.otp.dao;


import com.daniil.otp.entity.OtpCode;
import com.daniil.otp.entity.OtpState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByCode(String code);
    Optional<OtpCode> findByOperationId(String operationId);
    List<OtpCode> findByCreatedAtBeforeAndState(LocalDateTime dateTime, OtpState state);
}
