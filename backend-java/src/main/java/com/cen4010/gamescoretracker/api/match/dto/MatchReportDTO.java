package com.cen4010.gamescoretracker.api.match.dto;

import com.cen4010.gamescoretracker.api.user.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MatchReportDTO {

    private MatchDTO match;
    private List<MatchScoreDTO> scores;
    private List<UserDTO> updatedUsers;

    @Data
    @Builder
    public static class MatchDTO {
        private UUID matchId;
        private LocalDateTime matchDate;
        private String result;

        private UUID createdBy;
        private String createdByUsername;

        private UUID groupId;
        private String groupName;

        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class MatchScoreDTO {
        private UUID matchScoreId;
        private UUID userId;
        private String username;

        private int score;
        private String role; // WINNER, LOSER, TIE
    }
}
