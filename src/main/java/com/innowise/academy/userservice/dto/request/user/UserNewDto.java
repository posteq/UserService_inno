package com.innowise.academy.userservice.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UserNewDto {

    @NotBlank(message = "Firstname is required")
    private String firstname;

    @NotBlank(message = "Surname is required")
    private String surname;

    @DateTimeFormat
    private LocalDate birthday;

    @NotBlank(message = "Email is required")
    @Email(message = "Provide a valid email address")
    private String email;
}
