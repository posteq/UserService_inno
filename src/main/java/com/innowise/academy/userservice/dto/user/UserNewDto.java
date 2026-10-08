package com.innowise.academy.userservice.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserNewDto {
    private String firstname;
    private String surname;
    private LocalDate birthday;
    private String email;
}
