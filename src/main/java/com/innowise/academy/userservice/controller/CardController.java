package com.innowise.academy.userservice.controller;

import com.innowise.academy.userservice.dto.request.card.UpdateCardDto;
import com.innowise.academy.userservice.dto.response.card.CardResponseDto;
import com.innowise.academy.userservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService service;

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponseDto> getById(@PathVariable Long cardId){
        CardResponseDto card = service.getCard(cardId);

        return ResponseEntity.ok()
                .body(card);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardResponseDto> update(@PathVariable Long cardId,
                                                  @RequestBody @Valid UpdateCardDto cardDomain){
        CardResponseDto update = service.update(cardId, cardDomain);

        return ResponseEntity.ok()
                .body(update);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long cardId){
        service.delete(cardId);
        return ResponseEntity.noContent()
                .build();
    }
}
