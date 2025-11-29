package com.cen4010.gamescoretracker.api.match.dto;

import com.cen4010.gamescoretracker.api.user.dto.UserDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMatchesDTO {
    private List<UserMatchItemDTO> matches;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserMatchItemDTO {
        private UUID matchId;
        private LocalDateTime matchDate;
        private String result;
        private String playerRole;   // the role of the user requesting the matches
        private List<MatchUserEntryDTO> participants;
    }

    @Data
    @Builder
    public static class MatchUserEntryDTO {
        private UUID userId;
        private String username;
        private int score;
        private String role; // WINNER, LOSER, TIE
    }
}
