package com.cen4010.gamescoretracker.api.user.dto;

import lombok.Data;

@Data
public class UserStatsResponse {
    private int victories;
    private int defeats;
    private int matchesPlayed;
    private double winRate;
}