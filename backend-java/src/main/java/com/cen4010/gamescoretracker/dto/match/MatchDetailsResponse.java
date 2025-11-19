package com.cen4010.gamescoretracker.dto.match;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MatchDetailsResponse {
    private UUID matchId;
    private String date;
    private String result;
    private List<MatchPlayerScoreDTO> winners;
    private List<MatchPlayerScoreDTO> losers;
}
