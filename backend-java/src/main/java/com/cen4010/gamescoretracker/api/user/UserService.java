package com.cen4010.gamescoretracker.api.user;

import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.user.database.UserRepository;
import com.cen4010.gamescoretracker.api.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }


    public void updateUser(User user, UserUpdateRequest request) {

        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);
    }

    public void removeUserFromGroup(User targetUser) {
        targetUser.setGroup(null);
        targetUser.setGroupCode(null);
        userRepository.save(targetUser);
    }




}
