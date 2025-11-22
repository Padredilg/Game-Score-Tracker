package com.cen4010.gamescoretracker.api.auth.dto;

import com.cen4010.gamescoretracker.api.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserDTO user;
}
