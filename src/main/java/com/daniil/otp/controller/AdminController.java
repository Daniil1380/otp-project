package com.daniil.otp.controller;

import com.daniil.otp.service.AdminService;
import com.daniil.otp.entity.OtpConfig;
import com.daniil.otp.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/otp-config")
    public OtpConfig updateOtpConfig(@RequestBody OtpConfig config) {
        log.info("Запрос на обновление конфигурации OTP: {}", config);
        OtpConfig updatedConfig = adminService.updateOtpConfig(config);
        log.info("Конфигурация OTP успешно обновлена: {}", updatedConfig);
        return updatedConfig;
    }

    @GetMapping("/users")
    public List<User> getAllNonAdminUsers() {
        log.info("Запрос на получение всех не-админ пользователей");
        List<User> users = adminService.getAllNonAdminUsers();
        log.info("Найдено {} не-админ пользователей", users.size());
        return users;
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.warn("Запрос на удаление пользователя с ID: {}", id);
        adminService.deleteUserAndOtps(id);
        log.info("Пользователь с ID {} и его OTP коды были удалены", id);
    }
}