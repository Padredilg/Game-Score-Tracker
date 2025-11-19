package com.cen4010.gamescoretracker.dto.group;

import lombok.Data;

@Data
public class GroupVisibilityRequest {
    private Boolean cumulativeScoreVisibility;
    private Boolean highestScoreVisibility;
}