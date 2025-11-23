package com.cen4010.gamescoretracker.api.group;

import com.cen4010.gamescoretracker.api.group.dto.GroupDTO;
import com.cen4010.gamescoretracker.api.group.dto.JoinGroupRequest;
import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.user.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllGroups() {
        List<GroupDTO> groups = groupService.getAllGroupDTOs();

        if (groups.isEmpty()) {
            return ResponseEntity.status(404).body("No groups found");
        }

        return ResponseEntity.ok(groups);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinGroup(@RequestBody JoinGroupRequest request) {
        User currentUser = userService.getCurrentUser();
        groupService.joinGroup(currentUser, request.getGroupCode());
        return ResponseEntity.ok("User joined group successfully");
    }

}