package com.cen4010.gamescoretracker.api.match.dto;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class MatchCreateRequest {
    private String date;                // "MM/dd/yyyy"
    private String result;              // "win", "loss", "draw"
    private Map<UUID, Integer> winners; // userId -> score
    private Map<UUID, Integer> losers;  // userId -> score
}
