package com.cen4010.gamescoretracker.api.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}