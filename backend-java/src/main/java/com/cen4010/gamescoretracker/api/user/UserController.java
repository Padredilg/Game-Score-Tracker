package com.cen4010.gamescoretracker.api.user;

import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.user.dto.UserDTO;
import com.cen4010.gamescoretracker.api.user.dto.UserUpdateRequest;
import com.cen4010.gamescoretracker.utils.exceptions.ForbiddenAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET OWN USER INFO
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        var currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(UserMapper.toDTO(currentUser));
    }

    //GET USER (IN GROUP) INFO BY ID
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

    //UPDATE SELF (NICKNAME AND AVATAR)
    @PutMapping("/me/update")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserUpdateRequest request) {
        User currentUser = userService.getCurrentUser();
        userService.updateUser(currentUser, request);
        return ResponseEntity.ok(UserMapper.toDTO(currentUser));
    }

    //ADMIN REMOVE MEMBER FROM GROUP
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUserFromGroup(@PathVariable UUID id) {

        User admin = userService.getCurrentUser();
        User target = userService.getUserById(id);

        // admin must truly be group admin
        if (admin.getRole() != User.Role.ADMIN) {
            throw new ForbiddenAccessException("Only the group admin can remove users.");
        }

        // target must belong to a group
        if (target.getGroupCode() == null) {
            throw new ForbiddenAccessException("This user is not part of any group.");
        }

        // admin must be in the same group
        if (!target.getGroupCode().equals(admin.getGroupCode())) {
            throw new ForbiddenAccessException("You can only remove users from your own group.");
        }

        // Admin cannot delete themselves
        if (admin.getUserId().equals(target.getUserId())) {
            throw new ForbiddenAccessException("Group admin cannot remove themselves from the group.");
        }

        userService.removeUserFromGroup(target);

        return ResponseEntity.noContent().build();
    }

}