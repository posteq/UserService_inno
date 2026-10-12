package com.innowise.academy.userservice.dto.response.user;

import java.time.LocalDate;

public record UserResponseDto(
        Long id,
        String firstname,
        String surname,
        LocalDate birthday,
        String email,
        Boolean active) {
}
