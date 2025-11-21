package com.cen4010.gamescoretracker.dto.group;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.List;

@Data
@Builder
public class GroupDTO {
    private UUID groupId;
    private String groupCode;
    private String groupName;
    private List<String> usernames; // List of members' usernames
}
