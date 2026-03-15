package com.example.auth.config;

import com.example.auth.filter.JwtAuthFilter;
import com.example.auth.service.CustomOAuth2UserService;
import com.example.auth.service.FormLoginSuccessHandler;
import com.example.auth.service.OAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {

    private final JwtAuthFilter           jwtAuthFilter;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler    oAuth2SuccessHandler;
    private final FormLoginSuccessHandler formLoginSuccessHandler;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomOAuth2UserService oAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler, FormLoginSuccessHandler formLoginSuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.formLoginSuccessHandler = formLoginSuccessHandler;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.builder()
                .username("scrummaster")
                .password(passwordEncoder().encode("1234"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/logout",
                    "/api/auth/login", "/api/auth/logout",
                    "/api/auth/validate",
                    "/oauth2/**", "/login/oauth2/**",
                    "/actuator/health", "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // Form login — React POSTs credentials to /login
            .formLogin(form -> form
                .loginProcessingUrl("/login")
                .successHandler(formLoginSuccessHandler)
                .failureHandler((req, res, ex) -> {
                    res.sendRedirect("http://localhost:3000/?error=true");
                })
                .permitAll()
            )

            // OAuth2 login — React links to /oauth2/authorization/google etc.
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(ui -> ui.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureUrl("http://localhost:3000/?error=oauth_failed")
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    res.sendRedirect("http://localhost:3000/");
                })
                .deleteCookies("jwt_token", "JSESSIONID")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll()
            )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
            throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // Allow React dev server
        cfg.setAllowedOrigins(List.of("http://localhost:3000"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
