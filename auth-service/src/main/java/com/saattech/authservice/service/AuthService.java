package com.saattech.authservice.service;

import com.saattech.authservice.dto.request.AuthRequest;
import com.saattech.authservice.dto.request.RegisterRequest;
import com.saattech.authservice.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);
}

