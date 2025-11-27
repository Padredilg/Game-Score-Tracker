package com.cen4010.gamescoretracker.api.group.dto;

import lombok.Data;

@Data
public class GroupVisibilityRequest {
    private Boolean winPercentageVisibility;
    private Boolean matchesPlayedVisibility;
    private Boolean victoriesVisibility;
    private Boolean defeatsVisibility;
    private Boolean cumulativeScoreVisibility;
    private Boolean highestScoreVisibility;
}