package com.cen4010.gamescoretracker.services.auth;

import com.cen4010.gamescoretracker.dto.auth.LoginRequest;
import com.cen4010.gamescoretracker.dto.auth.LoginResponse;
import com.cen4010.gamescoretracker.dto.auth.RegisterRequest;
import com.cen4010.gamescoretracker.models.User;

public interface AuthService {
    User register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
