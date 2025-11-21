package com.cen4010.gamescoretracker.dto.auth;

import com.cen4010.gamescoretracker.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserDTO user;
}
