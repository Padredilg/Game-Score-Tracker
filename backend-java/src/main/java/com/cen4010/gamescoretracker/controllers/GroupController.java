package com.cen4010.gamescoretracker.controllers;

import com.cen4010.gamescoretracker.dto.group.GroupDTO;
import com.cen4010.gamescoretracker.services.GroupService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllGroups() {
        List<GroupDTO> groups = groupService.getAllGroupDTOs();

        if (groups.isEmpty()) {
            return ResponseEntity.status(404).body("No groups found");
        }

        return ResponseEntity.ok(groups);
    }

}