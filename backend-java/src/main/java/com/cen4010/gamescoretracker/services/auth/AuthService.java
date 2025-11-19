package com.cen4010.gamescoretracker.services.auth;


import com.cen4010.gamescoretracker.dto.auth.LoginRequest;
import com.cen4010.gamescoretracker.dto.auth.LoginResponse;
import com.cen4010.gamescoretracker.dto.auth.RegisterRequest;
import com.cen4010.gamescoretracker.exceptions.EntityExistsException;
import com.cen4010.gamescoretracker.exceptions.InvalidCredentialsException;
import com.cen4010.gamescoretracker.models.User;
import com.cen4010.gamescoretracker.repositories.UserRepository;
import com.cen4010.gamescoretracker.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<User> register(RegisterRequest request) {

        validateRegisterRequest(request);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setRole(User.Role.fromString(request.getRole()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        validateLoginRequest(request);

        //retrieve user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials."));

        //verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        // Validate role using the enum helper we created
        User.Role.fromString(request.getRole());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EntityExistsException("Username already taken.");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
    }
}
