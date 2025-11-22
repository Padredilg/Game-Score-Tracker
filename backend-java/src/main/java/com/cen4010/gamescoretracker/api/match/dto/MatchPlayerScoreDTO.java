package com.cen4010.gamescoretracker.api.match.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MatchPlayerScoreDTO {
    private UUID id;
    private String name;
    private int score;
}
