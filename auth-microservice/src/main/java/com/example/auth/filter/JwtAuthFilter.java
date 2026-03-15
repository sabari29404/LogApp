package com.example.auth.filter;

import com.example.auth.service.OAuth2SuccessHandler;
import com.example.auth.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Reads the Authorization: Bearer <token> header on every request.
 * If the JWT is valid, it populates the SecurityContext with the user + roles.
 * OAuth2 sessions are handled separately by Spring Security — this filter
 * only activates when a Bearer token is present.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && jwtUtil.isTokenValid(token)) {
            Claims claims  = jwtUtil.extractAllClaims(token);
            String subject = claims.getSubject();

            // Extract roles stored as claim
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
            log.debug("JWT authenticated: {}", subject);
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        // Also check cookie for browser-based flow
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
