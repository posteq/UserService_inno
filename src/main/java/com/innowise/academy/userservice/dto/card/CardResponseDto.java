package com.innowise.academy.userservice.dto.card;

import com.innowise.academy.userservice.dto.user.UserResponseDto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CardResponseDto {
    private Long id;
    private UserResponseDto user;
    private Long number;
    private String holder;
    private LocalDate expirationDate;
}
