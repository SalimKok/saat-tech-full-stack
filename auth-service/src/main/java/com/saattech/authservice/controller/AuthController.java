package com.saattech.authservice.controller;

import com.saattech.authservice.dto.request.AuthRequest;
import com.saattech.authservice.dto.request.RegisterRequest;
import com.saattech.authservice.dto.response.AuthResponse;
import com.saattech.authservice.service.AuthService;
import com.saattech.authservice.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return cookieUtil.buildCookieResponse(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse authResponse = authService.login(request);
        return cookieUtil.buildCookieResponse(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return cookieUtil.buildLogoutResponse();
    }
}
