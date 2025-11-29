package com.cen4010.gamescoretracker.api.user;

import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.user.dto.UserDTO;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .groupCode(user.getGroupCode())
                .avatarUrl(user.getAvatarUrl())
                .victories(user.getVictories())
                .defeats(user.getDefeats())
                .draws(user.getDraws())
                .matchesPlayed(user.getMatchesPlayed())
                .cumulativeScore(user.getCumulativeScore())
                .highestScore(user.getHighestScore())
                .build();
    }


    public static UserDTO toDTOWithWinPercentage(User user) {
        double winPct = (user.getMatchesPlayed() == 0)
                ? 0.0
                : (double) user.getVictories() / user.getMatchesPlayed();

        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .groupCode(user.getGroupCode())
                .avatarUrl(user.getAvatarUrl())
                .victories(user.getVictories())
                .defeats(user.getDefeats())
                .draws(user.getDraws())
                .matchesPlayed(user.getMatchesPlayed())
                .cumulativeScore(user.getCumulativeScore())
                .highestScore(user.getHighestScore())
                .winPercentage(winPct)
                .build();
    }

}