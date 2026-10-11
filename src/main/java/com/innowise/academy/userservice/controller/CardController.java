package com.innowise.academy.userservice.controller;

import com.innowise.academy.userservice.dto.card.CardResponseDto;
import com.innowise.academy.userservice.dto.card.UpdateCardDto;
import com.innowise.academy.userservice.service.CardService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards/")
@RequiredArgsConstructor
public class CardController {

    CardService service;

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteById(@PathVariable @NonNull Long cardId){
        service.delete(cardId);
        return ResponseEntity.noContent()
                .build();
    }


    @PatchMapping("/{cardId}")
    public ResponseEntity<CardResponseDto> update(@PathVariable @NonNull Long cardId,
                                                  @RequestBody @Valid UpdateCardDto cardDomain){
        CardResponseDto update = service.update(cardId, cardDomain);

        return ResponseEntity.ok()
                .body(update);
    }

    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<CardResponseDto>> getCardByUserId(@PathVariable @NonNull Long userId){
        List<CardResponseDto> cardsByUserId = service.getCardsByUserId(userId);

        return ResponseEntity.ok()
                .body(cardsByUserId);
    }
}
