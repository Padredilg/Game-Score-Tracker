package com.cen4010.gamescoretracker.services;


import com.cen4010.gamescoretracker.dto.auth.LoginRequest;
import com.cen4010.gamescoretracker.dto.auth.LoginResponse;
import com.cen4010.gamescoretracker.dto.auth.RegisterRequest;
import com.cen4010.gamescoretracker.dto.user.UserDTO;
import com.cen4010.gamescoretracker.exceptions.EntityExistsException;
import com.cen4010.gamescoretracker.exceptions.InvalidCredentialsException;
import com.cen4010.gamescoretracker.models.User;
import com.cen4010.gamescoretracker.repositories.UserRepository;
import com.cen4010.gamescoretracker.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final GroupService groupService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<UserDTO> register(RegisterRequest request) {

        validateRegisterRequest(request);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setRole(User.Role.fromString(request.getRole()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == User.Role.ADMIN) {
            groupService.createGroupForAdmin(savedUser);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToUserDTO(savedUser));
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
        LoginResponse response = new LoginResponse(token, mapToUserDTO(user));

        return ResponseEntity.ok(response);
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

    private UserDTO mapToUserDTO(User savedUser) {
        return UserDTO.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .groupCode(savedUser.getGroupCode())
                .victories(savedUser.getVictories())
                .matchesPlayed(savedUser.getMatchesPlayed())
                .defeats(savedUser.getDefeats())
                .cumulativeScore(savedUser.getCumulativeScore())
                .highestScore(savedUser.getHighestScore())
                .build();
    }

    public User getCurrentUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof User) {
            username = ((User) principal).getUsername(); // directly return your User
        } else {
            throw new IllegalStateException("Principal is not a User instance");
        }


        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(Collections.emptyList()) // or add roles if you want
                .build();
    }

}
