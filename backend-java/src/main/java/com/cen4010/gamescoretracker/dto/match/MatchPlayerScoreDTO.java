package com.cen4010.gamescoretracker.dto.match;

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
