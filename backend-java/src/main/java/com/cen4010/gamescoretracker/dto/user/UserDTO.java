package com.cen4010.gamescoretracker.dto.user;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID userId;
    private String username;
    private String role;
    private String groupCode;
    private Integer victories;
    private Integer matchesPlayed;
    private Integer defeats;
    private Integer cumulativeScore;
    private Integer highestScore;
}
