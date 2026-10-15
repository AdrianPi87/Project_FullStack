package com.demo.configurator.service;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.model.User;
import com.demo.configurator.repository.UserRepository;
import com.demo.configurator.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public Dtos.AuthResponse register(Dtos.RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setDisplayName(req.displayName());
        user.setRole("USER");
        userRepository.save(user);
        return buildResponse(user);
    }

    @Transactional(readOnly = true)
    public Dtos.AuthResponse login(Dtos.LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return buildResponse(user);
    }

    private Dtos.AuthResponse buildResponse(User user) {
        return new Dtos.AuthResponse(jwtService.generateToken(String.valueOf(user.getId()), user.getRole()),
                user.getId(), user.getEmail(), user.getRole());
    }
}
