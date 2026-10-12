package com.innowise.academy.userservice.dto.response.user;

import com.innowise.academy.userservice.entity.PaymentCardEntity;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@JsonSerialize
public class UserResponseDtoWithCards{
    private Long id;
    private String firstname;
    private String surname;
    private LocalDate birthday;
    private String email;
    private Boolean active;
    private List<PaymentCardEntity> cards;
}
