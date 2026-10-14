package com.innowise.academy.userservice.controller;

import com.innowise.academy.userservice.dto.request.card.CardNewDto;
import com.innowise.academy.userservice.dto.request.user.UpdateUserRequest;
import com.innowise.academy.userservice.dto.request.user.UserNewDto;
import com.innowise.academy.userservice.dto.response.card.CardShortResponseDto;
import com.innowise.academy.userservice.dto.response.user.UserResponseDto;
import com.innowise.academy.userservice.dto.response.user.UserResponseDtoWithCards;
import com.innowise.academy.userservice.service.CardService;
import com.innowise.academy.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserNewDto userNewDto){
        UserResponseDto userResponseDto = userService.create(userNewDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponseDto.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(userResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getUsers(@RequestParam(required = false) String firstname,
                                                          @RequestParam(required = false) String surname,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") @Max(100) int size,
                                                          @RequestParam(defaultValue = "firstname") String sortBy,
                                                          @RequestParam(defaultValue = "ASC") String sortDir){
        Page<UserResponseDto> users = userService.getUsers(firstname, surname, page, size, sortBy, sortDir);
        return ResponseEntity.ok()
                .body(users);

    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDtoWithCards> getById(@PathVariable Long userId){
        UserResponseDtoWithCards user = userService.getUser(userId);
        return ResponseEntity.ok()
                .body(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long userId,
                                                  @RequestBody @Valid UpdateUserRequest userDomain){
        UserResponseDto update = userService.update(userId, userDomain);

        return ResponseEntity.ok()
                .body(update);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long userId){
        userService.delete(userId);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/{userId}/active")
    public ResponseEntity<Void> changeUserStatus(@PathVariable Long userId,
                                                 @RequestParam Boolean active){
        userService.changeStatus(active,userId);
        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/{userId}/cards")
    public ResponseEntity<Void> addCardToUser(@PathVariable Long userId,
                                              @RequestBody @Valid CardNewDto cardNewDto){

        cardService.addCardToUsers(userId,cardNewDto);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<CardShortResponseDto>> getCardByUserId(@PathVariable Long userId){
        List<CardShortResponseDto> cardsByUserId = cardService.getCardsByUserId(userId);

        return ResponseEntity.ok()
                .body(cardsByUserId);
    }
}
