package com.cen4010.gamescoretracker.api.user.dto;

import com.cen4010.gamescoretracker.api.user.database.User;
import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private UUID userId;
    private String username;
    private String nickname;
    private User.Role role;
    private String groupCode;
    private String avatarUrl;
    private int victories;
    private int defeats;
    private int matchesPlayed;
    private int cumulativeScore;
    private int highestScore;
}
