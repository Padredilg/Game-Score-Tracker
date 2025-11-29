package com.cen4010.gamescoretracker.api.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // "admin" or "regular"
}
