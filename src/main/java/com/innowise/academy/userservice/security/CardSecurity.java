package com.innowise.academy.userservice.security;

import com.innowise.academy.userservice.repository.PaymentCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardSecurity {

    private final PaymentCardRepository repository;

    public boolean isCurrentUser(Long cardId, Jwt jwt) {
        Number jwtUserId = jwt.getClaim("user_id");
        if(jwtUserId != null){
            return repository.existsByIdAndUser_Id(cardId, jwtUserId.longValue());
        }
        return false;
    }
}
