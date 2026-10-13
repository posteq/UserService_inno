package com.innowise.academy.userservice.dto.response.card;

import java.time.LocalDate;

public record CardResponseDto(
        Long id,
        Long userId,
        Long number,
        String holder,
        LocalDate expirationDate){}
