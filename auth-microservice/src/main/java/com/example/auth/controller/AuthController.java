package com.example.auth.controller;

import com.example.auth.dto.AuthDtos.*;
import com.example.auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil               jwtUtil;

    // POST /api/auth/login — React login form calls this
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> apiLogin(@Valid @RequestBody LoginRequest req,
                                      HttpServletResponse response) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    req.usernameOrEmail(), req.password());
            Authentication auth = authManager.authenticate(authToken);
            UserDetails user = (UserDetails) auth.getPrincipal();

            String token = jwtUtil.generateToken(user.getUsername(), Map.of(
                    "name",     user.getUsername(),
                    "roles",    List.of("USER"),
                    "provider", "local"
            ));

            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(new AuthResponse(
                    token, user.getUsername(), "USER",
                    jwtUtil.getExpirationMs() / 1000 + "s", "Login successful"
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Invalid username or password"));
        }
    }

    // GET /api/auth/me — React calls this to get logged-in user info
    @GetMapping("/api/auth/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }
        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "roles",    authentication.getAuthorities()
        ));
    }

    // GET /api/auth/validate — validate JWT token
    @GetMapping("/api/auth/validate")
    public ResponseEntity<?> validate(
            @RequestHeader(value = "Authorization", required = false) String bearer) {
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "reason", "Missing Bearer token"));
        }
        String token = bearer.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "reason", "Token invalid or expired"));
        }
        return ResponseEntity.ok(Map.of(
                "valid",   true,
                "subject", jwtUtil.extractSubject(token)
        ));
    }

    // POST /api/auth/logout — React calls this to clear cookie
    @PostMapping("/api/auth/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
