package com.cen4010.gamescoretracker.api.user;

import com.cen4010.gamescoretracker.api.user.dto.UserDTO;
import com.cen4010.gamescoretracker.utils.exceptions.ForbiddenAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        var currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(UserMapper.toDTO(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {

        var currentUser = userService.getCurrentUser();
        var targetUser = userService.getUserById(id);

        // Authorization: must share groupCode
        if (currentUser.getGroupCode() == null ||
                targetUser.getGroupCode() == null ||
                !currentUser.getGroupCode().equals(targetUser.getGroupCode())) {

            throw new ForbiddenAccessException(
                    "Access denied: You can only view users who belong to the same group."
            );
        }

        return ResponseEntity.ok(UserMapper.toDTO(targetUser));
    }
}