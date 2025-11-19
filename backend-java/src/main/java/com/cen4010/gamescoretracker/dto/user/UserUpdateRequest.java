package com.cen4010.gamescoretracker.dto.user;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String avatarUrl;
}