package com.cen4010.gamescoretracker.api.match.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class MatchCreateRequest {
    private String date;                 // mm/dd/yyyy
    private List<UUID> winners;
    private List<UUID> losers;
    private String result;              // win, loss, draw
    private Map<UUID, Integer> scores;  // playerId -> score
}
