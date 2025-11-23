package com.cen4010.gamescoretracker.api.user;

import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.user.dto.UserDTO;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .groupCode(user.getGroupCode())
                .avatarUrl(user.getAvatarUrl())
                .victories(user.getVictories())
                .defeats(user.getDefeats())
                .matchesPlayed(user.getMatchesPlayed())
                .cumulativeScore(user.getCumulativeScore())
                .highestScore(user.getHighestScore())
                .build();
    }
}