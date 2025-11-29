package com.cen4010.gamescoretracker.api.group;

import com.cen4010.gamescoretracker.api.group.database.Group;
import com.cen4010.gamescoretracker.api.group.dto.*;
import com.cen4010.gamescoretracker.api.user.UserMapper;
import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.user.UserService;
import com.cen4010.gamescoretracker.api.user.dto.UserDTO;
import com.cen4010.gamescoretracker.utils.requireadmin.RequireAdmin;
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

    // FOR REGULAR USER TO JOIN GROUP
    @PostMapping("/join")
    public ResponseEntity<UserDTO> joinGroup(@RequestBody JoinGroupRequest request) {
        User currentUser = userService.getCurrentUser();
        var updatedUser = groupService.joinGroup(currentUser, request.getGroupCode());
        return ResponseEntity.ok(UserMapper.toDTO(updatedUser));
    }

    // UPDATE GROUP COLUMN VISIBILITY
    @RequireAdmin
    @PutMapping("/togglevisibility")
    public ResponseEntity<GroupDTO> toggleVisibility(@RequestBody GroupVisibilityRequest request) {
        User currentUser = userService.getCurrentUser();
        var updatedGroup = groupService.toggleVisibility(currentUser, request);
        return ResponseEntity.ok(GroupMapper.toDTO(updatedGroup));
    }

    // UPDATE GROUP'S NAME
    @RequireAdmin
    @PutMapping("/editname")
    public ResponseEntity<GroupDTO> editGroupName(@RequestBody GroupEditNameRequest request) {
        User currentUser = userService.getCurrentUser();
        var updatedGroup = groupService.editGroupName(currentUser, request);
        return ResponseEntity.ok(GroupMapper.toDTO(updatedGroup));
    }

    // OPEN/CLOSE GROUP FOR NEW MEMBERS
    @RequireAdmin
    @PutMapping("/manage")
    public ResponseEntity<GroupDTO> manageGroup(@RequestBody GroupManageRequest request) {
        User currentUser = userService.getCurrentUser();
        var updatedGroup = groupService.manageGroup(currentUser, request);
        return ResponseEntity.ok(GroupMapper.toDTO(updatedGroup));
    }


    @GetMapping("/groupdetails")
    public ResponseEntity<GroupDetailsResponse> getGroupDetails() {
        User currentUser = userService.getCurrentUser();
        Group group = groupService.getGroupForUser(currentUser);

        GroupDTO groupDTO = GroupMapper.toDTO(group);

        List<UserDTO> members = group.getUsers().stream()
                .map(UserMapper::toDTOWithWinPercentage)
                .toList();

        GroupDetailsResponse response = GroupDetailsResponse.builder()
                .group(groupDTO)
                .members(members)
                .build();

        return ResponseEntity.ok(response);
    }

    //Retrieve all existing groups (not for app)
    @GetMapping("/all")
    public ResponseEntity<?> getAllGroups() {
        List<GroupDTO> groups = groupService.getAllGroupDTOs();

        if (groups.isEmpty()) {
            return ResponseEntity.status(404).body("No groups found");
        }

        return ResponseEntity.ok(groups);
    }

}