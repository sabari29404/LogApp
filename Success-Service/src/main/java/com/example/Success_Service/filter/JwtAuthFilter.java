package com.example.Success_Service.filter;

import com.example.Success_Service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && jwtUtil.isTokenValid(token)) {
            Claims claims  = jwtUtil.extractAllClaims(token);
            String subject = claims.getSubject();

            Object rolesClaim = claims.get("roles");
            List<SimpleGrantedAuthority> authorities;
            if (rolesClaim instanceof List<?> list) {
                authorities = list.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toString().toUpperCase()))
                        .toList();
            } else {
                authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }

            var auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("JWT authenticated in Success-Service: {}", subject);
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // Check Authorization header
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        // Check cookie (set by auth-service after login)
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
