package com.innowise.academy.userservice.dto.response.user;

import com.innowise.academy.userservice.dto.response.card.CardShortResponseDto;

import java.time.LocalDate;
import java.util.List;


public record UserResponseDtoWithCards(Long id,
String firstname,
String surname,
LocalDate birthday,
String email,
Boolean active,
List<CardShortResponseDto> cards
){}
