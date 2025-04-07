package com.daniil.otp.service;




import com.daniil.otp.controller.dto.AuthRequest;
import com.daniil.otp.controller.dto.AuthResponse;
import com.daniil.otp.controller.dto.RegisterRequest;
import com.daniil.otp.dao.UserRepository;
import com.daniil.otp.entity.User;
import com.daniil.otp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (request.role.equalsIgnoreCase("ADMIN")) {
            boolean adminExists = userRepository.findAll()
                    .stream()
                    .anyMatch(user -> user.getRole().equalsIgnoreCase("ADMIN"));
            if (adminExists) {
                throw new RuntimeException("Администратор уже существует");
            }
        }

        User user = User.builder()
                .login(request.login)
                .encryptedPassword(passwordEncoder.encode(request.password))
                .role(request.role.toUpperCase())
                .email(request.email)
                .phoneNumber(request.phoneNumber)
                .telegram(request.telegram)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(Map.of(), user.getLogin());
        return new AuthResponse(token);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login, request.password)
        );

        User user = userRepository.findByLogin(request.login)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String token = jwtService.generateToken(Map.of(), user.getLogin());
        return new AuthResponse(token);
    }
}
