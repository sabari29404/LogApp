package com.example.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * After a successful Google/GitHub OAuth2 login, this service is called.
 * We load the OAuth2 user and assign ROLE_USER to them.
 * No database persistence needed — we use in-memory for local users
 * and just process OAuth2 users on-the-fly.
 */

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = delegate.loadUser(request);

        String provider = request.getClientRegistration().getRegistrationId();
        Map<String, Object> attrs = oauthUser.getAttributes();

        String email = (String) attrs.getOrDefault("email", "unknown@oauth.com");
        String name  = (String) attrs.getOrDefault("name", email);

        log.info("OAuth2 login: {} via {}", email, provider);

        // Grant ROLE_USER to all OAuth2 users
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // Use "sub" for Google, "id" for GitHub as the name-attribute key
        String nameAttrKey = attrs.containsKey("sub") ? "sub" : "id";

        return new DefaultOAuth2User(authorities, attrs, nameAttrKey);
    }
}
