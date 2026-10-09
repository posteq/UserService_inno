package com.innowise.academy.userservice.controller;

import com.innowise.academy.userservice.dto.card.CardNewDto;
import com.innowise.academy.userservice.dto.card.CardResponseDto;
import com.innowise.academy.userservice.dto.user.UpdateUserRequest;
import com.innowise.academy.userservice.dto.user.UserNewDto;
import com.innowise.academy.userservice.dto.user.UserResponseDto;
import com.innowise.academy.userservice.service.CardService;
import com.innowise.academy.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable @NonNull Long userId){
        UserResponseDto user = userService.getUser(userId);
        return ResponseEntity.ok()
                .body(user);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteById(@PathVariable @NonNull Long userId){
        userService.delete(userId);
        return ResponseEntity.noContent()
                .build();
    }


    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserNewDto userNewDto){
        UserResponseDto userResponseDto = userService.create(userNewDto);
        return ResponseEntity.ok()
                .body(userResponseDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(@PathVariable @NonNull Long userId,
                                                  @RequestBody @Valid UpdateUserRequest userDomain){
        UserResponseDto update = userService.update(userId, userDomain);

        return ResponseEntity.ok()
                .body(update);
    }

    @PatchMapping("/{userId}/active")
    public ResponseEntity<UserResponseDto> changeUserStatus(@RequestParam @NonNull Boolean status,
                                                            @PathVariable @NonNull Long userId){
        userService.changeStatus(status,userId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers(@RequestParam(defaultValue = "10") String firstname,
                                                          @RequestParam(defaultValue = "10") String surname,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "firstname") String sortBy,
                                                          @RequestParam(defaultValue = "ASC") String sortDir){
        List<UserResponseDto> users = userService.getUsers(firstname, surname, page, size, sortBy, sortDir);
        return ResponseEntity.ok()
                .body(users);

    }

    @PatchMapping("/{userId}/cards")
    public ResponseEntity<UserResponseDto> addCard(@RequestPart @NonNull Long userId,
                                                   @RequestBody @Valid CardNewDto cardNewDto){


        CardResponseDto cardResponseDto = cardService.addCardToUsers(userId,cardNewDto);
        return ResponseEntity.ok()
                .build();
    }
}
