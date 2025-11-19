package com.cen4010.gamescoretracker.controllers;

import com.cen4010.gamescoretracker.dto.auth.LoginRequest;
import com.cen4010.gamescoretracker.dto.auth.LoginResponse;
import com.cen4010.gamescoretracker.dto.auth.RegisterRequest;
import com.cen4010.gamescoretracker.models.User;
import com.cen4010.gamescoretracker.services.auth.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}