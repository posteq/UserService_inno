package com.innowise.academy.userservice.dto.request.card;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateCardDto {
    private String holder;
    private LocalDate expirationDate;
}
