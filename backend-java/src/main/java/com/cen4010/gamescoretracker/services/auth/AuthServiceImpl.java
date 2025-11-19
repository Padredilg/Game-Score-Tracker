package com.cen4010.gamescoretracker.services.auth;


import com.cen4010.gamescoretracker.dto.auth.LoginRequest;
import com.cen4010.gamescoretracker.dto.auth.LoginResponse;
import com.cen4010.gamescoretracker.dto.auth.RegisterRequest;
import com.cen4010.gamescoretracker.models.User;
import com.cen4010.gamescoretracker.repositories.UserRepository;
import com.cen4010.gamescoretracker.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public User register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }

        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setRole(User.Role.valueOf(request.getRole().toLowerCase()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials.");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token);
    }
}
