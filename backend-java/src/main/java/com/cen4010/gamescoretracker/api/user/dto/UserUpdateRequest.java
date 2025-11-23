package com.cen4010.gamescoretracker.api.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String avatarUrl;
}