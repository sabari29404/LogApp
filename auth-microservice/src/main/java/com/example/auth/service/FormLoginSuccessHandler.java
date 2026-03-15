package com.example.auth.service;

import com.example.auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Component

public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(FormLoginSuccessHandler.class);

    @Autowired
    public FormLoginSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();

        String token = jwtUtil.generateToken(username, Map.of(
                "name",     username,
                "roles",    List.of("USER"),
                "provider", "local"
        ));

        Cookie jwtCookie = new Cookie("jwt_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
        response.addCookie(jwtCookie);

        log.info("Form login success — issued JWT for: {}", username);
        // Redirect to React success page
        response.sendRedirect("http://localhost:3000/success");
    }
}
