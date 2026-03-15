package com.example.auth.service;

import com.example.auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attrs = oauthUser.getAttributes();

        String provider = "oauth2";
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            provider = oauthToken.getAuthorizedClientRegistrationId();
        }

        String email = (String) attrs.getOrDefault("email", "oauth2user");
        String name  = (String) attrs.getOrDefault("name", email);

        if (email == null || email.isBlank()) {
            email = (String) attrs.getOrDefault("login", "oauth2user");
        }
        if (name == null || name.isBlank()) {
            name = email;
        }

        String token = jwtUtil.generateToken(email, Map.of(
                "name",     name,
                "roles",    List.of("USER"),
                "provider", provider
        ));

        Cookie jwtCookie = new Cookie("jwt_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
        response.addCookie(jwtCookie);

        log.info("OAuth2 success — issued JWT for: {} via {}", email, provider);
        // Redirect to React success page
        response.sendRedirect("http://localhost:3000/success");
    }
}
