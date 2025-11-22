package com.cen4010.gamescoretracker.api.group.dto;

import lombok.Data;

@Data
public class GroupVisibilityRequest {
    private Boolean cumulativeScoreVisibility;
    private Boolean highestScoreVisibility;
}