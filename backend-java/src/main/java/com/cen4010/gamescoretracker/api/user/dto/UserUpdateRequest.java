package com.cen4010.gamescoretracker.api.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String nickname;
    private String avatarUrl;
}