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
        private String result;       // WIN, LOSS, DRAW (global match result)
        private String playerRole;   // WINNER, LOSER, TIE (role for the requested user)
        private List<UserDTO> winners;
        private List<UserDTO> losers;
        private List<UserDTO> ties;
    }
}
