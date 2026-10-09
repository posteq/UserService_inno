package com.innowise.academy.userservice.dto.card;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardNewDto {
    private Long number;
    private String holder;
    private LocalDate expirationDate;
}
