package com.cen4010.gamescoretracker.api.group.dto;

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
    private List<String> usernames;
    private Boolean openForNewMembers;
    private Boolean winPercentageVisibility;
    private Boolean matchesPlayedVisibility;
    private Boolean victoriesVisibility;
    private Boolean defeatsVisibility;
    private Boolean cumulativeScoreVisibility;
    private Boolean highestScoreVisibility;
}
