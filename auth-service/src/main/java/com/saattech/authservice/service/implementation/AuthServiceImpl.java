package com.saattech.authservice.service.implementation;

import com.saattech.authservice.dto.request.AuthRequest;
import com.saattech.authservice.dto.request.RegisterRequest;
import com.saattech.authservice.dto.response.AuthResponse;
import com.saattech.authservice.entity.User;
import com.saattech.authservice.enums.Role;
import com.saattech.authservice.repository.UserRepository;
import com.saattech.authservice.security.CustomUserDetails;
import com.saattech.authservice.service.AuthService;
import com.saattech.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());

        CustomUserDetails customUser = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(extraClaims, customUser);

        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .message("Registration successful.")
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());

        CustomUserDetails customUser = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(extraClaims, customUser);

        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .message("Login successful.")
                .build();
    }
}
