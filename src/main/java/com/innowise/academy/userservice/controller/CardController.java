package com.innowise.academy.userservice.controller;

import com.innowise.academy.userservice.dto.request.card.CardNewDto;
import com.innowise.academy.userservice.dto.request.card.UpdateCardDto;
import com.innowise.academy.userservice.dto.response.card.CardResponseDto;
import com.innowise.academy.userservice.dto.response.card.CardShortResponseDto;
import com.innowise.academy.userservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardService service;

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userSecurity.isCurrentUser(#cardId, authentication.principal))")
    @GetMapping("/cards/{cardId}")
    public ResponseEntity<CardResponseDto> getById(@PathVariable Long cardId){
        CardResponseDto card = service.getCard(cardId);

        return ResponseEntity.ok()
                .body(card);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/cards/{cardId}")
    public ResponseEntity<CardResponseDto> update(@PathVariable Long cardId,
                                                  @RequestBody @Valid UpdateCardDto cardDomain){
        CardResponseDto update = service.update(cardId, cardDomain);

        return ResponseEntity.ok()
                .body(update);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long cardId){
        service.delete(cardId);
        return ResponseEntity.noContent()
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userSecurity.isCurrentUser(#userId, authentication.principal))")
    @PostMapping("/users/{userId}/cards")
    public ResponseEntity<Void> addCardToUser(@PathVariable Long userId,
                                              @RequestBody @Valid CardNewDto cardNewDto){

        service.addCardToUsers(userId,cardNewDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userSecurity.isCurrentUser(#userId, authentication.principal))")
    @GetMapping("/users/{userId}/cards")
    public ResponseEntity<List<CardShortResponseDto>> getCardByUserId(@PathVariable Long userId){

        List<CardShortResponseDto> cardsByUserId = service.getCardsByUserId(userId);

        return ResponseEntity.ok()
                .body(cardsByUserId);
    }
}
