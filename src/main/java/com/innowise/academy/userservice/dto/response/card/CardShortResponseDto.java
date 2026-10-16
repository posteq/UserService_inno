package com.innowise.academy.userservice.dto.response.card;

import java.time.LocalDate;

public record CardShortResponseDto(
        Long id,
        Long number,
        String holder,
        LocalDate expirationDate){}
