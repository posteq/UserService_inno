package com.innowise.academy.userservice.dto.response.card;

import com.innowise.academy.userservice.dto.response.user.UserResponseDtoWithCards;

import java.time.LocalDate;

public record CardResponseDto(
        Long id,
        UserResponseDtoWithCards user,
        Long number,
        String holder,
        LocalDate expirationDate){}
