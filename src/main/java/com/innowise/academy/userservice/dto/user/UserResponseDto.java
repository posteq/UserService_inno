package com.innowise.academy.userservice.dto.user;

import com.innowise.academy.userservice.entity.PaymentCardEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthday;
    private String email;
    private Boolean active;
    private List<PaymentCardEntity> cards;
}
