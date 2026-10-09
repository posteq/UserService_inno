package com.innowise.academy.userservice.controller;

import com.innowise.academy.userservice.dto.user.UserResponseDto;
import com.innowise.academy.userservice.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/{userId}")
    public RequestEntity<UserResponseDto> getById(@PathVariable @NonNull Long userId){
        UserResponseDto user = service.getUser(userId);
        return ResponseEntity.ok()
                .body(user);
    }
}
