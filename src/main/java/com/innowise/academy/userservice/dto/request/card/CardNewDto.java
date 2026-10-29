package com.innowise.academy.userservice.dto.request.card;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CardNewDto {

    @NotNull(message = "Card number is required")
    @Min(value = 1000000000000000L, message = "The card number must be exactly 16 digits")
    @Max(value = 9999999999999999L, message = "The card number must be exactly 16 digits")
    private Long number;

    @NotBlank(message = "Card holder name is required")
    private String holder;

    @NotNull(message = "Expiration date is required")
    @Future(message = "The card expiration date must be in the future")
    private LocalDate expirationDate;
}
