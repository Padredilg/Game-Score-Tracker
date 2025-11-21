package com.cen4010.gamescoretracker.services;

import com.cen4010.gamescoretracker.models.User;
import com.cen4010.gamescoretracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return userRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }
        throw new IllegalStateException("Principal is not a User instance");
    }
}
