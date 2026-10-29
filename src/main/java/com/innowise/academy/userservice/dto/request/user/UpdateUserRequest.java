package com.innowise.academy.userservice.dto.request.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserRequest {
    private String firstname;
    private String surname;
    @DateTimeFormat
    private LocalDate birthday;
    private String email;
}
