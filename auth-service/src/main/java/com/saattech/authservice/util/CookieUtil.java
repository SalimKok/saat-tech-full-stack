package com.saattech.authservice.util;

import com.saattech.authservice.config.JwtProperties;
import com.saattech.authservice.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtProperties jwtProperties;

    public ResponseEntity<AuthResponse> buildCookieResponse(AuthResponse authResponse) {
        ResponseCookie springCookie = ResponseCookie.from("jwt", authResponse.getToken())
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite(jwtProperties.getCookieSameSite())
                .path("/")
                .maxAge(jwtProperties.getExpiration())
                .build();

        authResponse.setToken(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(authResponse);
    }

    public ResponseEntity<Void> buildLogoutResponse() {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite(jwtProperties.getCookieSameSite())
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}
