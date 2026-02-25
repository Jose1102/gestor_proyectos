package com.davivienda.service;

import com.davivienda.dto.AuthResponse;
import com.davivienda.dto.LoginRequest;
import com.davivienda.dto.RegisterRequest;
import com.davivienda.model.User;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    User getCurrentUser(String email);
}

