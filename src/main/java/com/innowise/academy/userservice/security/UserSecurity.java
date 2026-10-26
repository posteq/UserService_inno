package com.innowise.academy.userservice.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {
    public boolean isCurrentUser(Long userId, Jwt jwt) {
        Number jwtUserId = jwt.getClaim("user_id");

        return jwtUserId != null && jwtUserId.longValue() == userId;
    }
}
