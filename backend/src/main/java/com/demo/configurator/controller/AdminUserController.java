package com.demo.configurator.controller;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.model.User;
import com.demo.configurator.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<Dtos.UserResponse> users() {
        return userRepository.findAll().stream()
                .map(u -> new Dtos.UserResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getRole(), u.getCreatedAt()))
                .toList();
    }
}
