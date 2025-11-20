package com.cen4010.gamescoretracker.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}