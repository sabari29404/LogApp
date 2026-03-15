package com.example.Success_Service.controller;

import com.example.Success_Service.entity.CompanyEntity;
import com.example.Success_Service.service.CompanyService;
import com.example.Success_Service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SuccessController {

    private final CompanyService companyService;
    private final JwtUtil jwtUtil;

    // GET /api/user — returns authenticated user info as JSON
    @GetMapping("/api/user")
    public ResponseEntity<?> userInfo(Authentication authentication,
                                      HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        String name     = authentication.getName();
        String email    = name;
        String provider = "local";

        String token = resolveToken(request);
        if (token != null && jwtUtil.isTokenValid(token)) {
            Claims claims    = jwtUtil.extractAllClaims(token);
            String claimName = claims.get("name",     String.class);
            String claimProv = claims.get("provider", String.class);
            if (claimName != null) name     = claimName;
            if (claimProv != null) provider = claimProv;
        }

        log.info("User info requested by: {}", authentication.getName());
        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "name",     name,
                "email",    email,
                "provider", provider,
                "role",     "ROLE_USER"
        ));
    }

    // GET /api/companies — returns company list as JSON
    @GetMapping("/api/companies")
    public ResponseEntity<List<CompanyEntity>> companies(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        log.info("Companies requested by: {}", authentication.getName());
        return ResponseEntity.ok(companyService.getCompanyDetails());
    }

    private String resolveToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) return cookie.getValue();
            }
        }
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) return bearer.substring(7);
        return null;
    }
}
