package com.innowise.academy.userservice.service;

import com.innowise.academy.userservice.dto.request.user.UpdateUserRequest;
import com.innowise.academy.userservice.dto.request.user.UserNewDto;
import com.innowise.academy.userservice.dto.response.user.UserResponseDto;
import com.innowise.academy.userservice.dto.response.user.UserResponseDtoWithCards;
import com.innowise.academy.userservice.entity.UserEntity;
import com.innowise.academy.userservice.exception.DuplicateCreationException;
import com.innowise.academy.userservice.exception.UserNotFoundException;
import com.innowise.academy.userservice.mapper.UserMapper;
import com.innowise.academy.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserMapper userMapper;
    @Mock
    UserRepository repository;
    @InjectMocks
    UserService userService;


    private static UserEntity entity(Long id) {
        UserEntity e = new UserEntity();
        e.setId(id);
        e.setFirstname("Ann");
        e.setSurname("Lee");
        e.setEmail("ann@mail.com");
        e.setActive(true);
        return e;
    }

    private static UserResponseDto dto(Long id) {
        return new UserResponseDto(id, "Ann", "Lee", LocalDate.of(1990, 1, 1), "ann@mail.com", true);
    }

    @Test
    void invalidSortField_throwsAndDoesNotTouchRepository() {
        //given


        //when


        //then
        assertThatThrownBy(() -> userService.getUsers(null, null, 0, 10, "hack", "ASC"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void found_returnsDtoWithCards() {
        //given
        UserEntity e = entity(1L);
        UserResponseDtoWithCards expected = new UserResponseDtoWithCards(
                1L, "Ann", "Lee", LocalDate.of(1990, 1, 1), "ann@mail.com", true, List.of());

        //when
        when(repository.findWithCardsById(1L)).thenReturn(Optional.of(e));
        when(userMapper.userToResponseDtoWithCards(e)).thenReturn(expected);

        //then
        assertThat(userService.getUser(1L)).isEqualTo(expected);
    }

    @Test
    void notFound_throws() {
        //given


        //when
        when(repository.findWithCardsById(1L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> userService.getUser(1L))
                .isInstanceOf(UserNotFoundException.class);
        verifyNoInteractions(userMapper);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void updated_callsRepository(boolean active) {
        //given


        //when
        when(repository.changeActiveById(1L, active)).thenReturn(1);

        //then
        userService.changeStatus(active, 1L);
        verify(repository).changeActiveById(1L, active);
    }

    @Test
    void noRowsUpdated_throwsNotFound() {
        //given


        //when
        when(repository.changeActiveById(1L, true)).thenReturn(0);

        //then
        assertThatThrownBy(() -> userService.changeStatus(true, 1L))
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    void ok_savesAndReturnsDto() {
        //given
        UserNewDto request = mock(UserNewDto.class);
        UserEntity toSave = entity(null);
        UserEntity saved = entity(1L);

        //when
        when(request.getEmail()).thenReturn("ann@mail.com");
        when(repository.existsByEmail("ann@mail.com")).thenReturn(false);
        when(userMapper.userNewDtoToUser(request)).thenReturn(toSave);
        when(repository.save(toSave)).thenReturn(saved);
        when(userMapper.userToResponseDto(saved)).thenReturn(dto(1L));

        //then
        assertThat(userService.create(request)).isEqualTo(dto(1L));
        verify(repository).save(toSave);
    }

    @Test
    void duplicateEmail_throwsAndDoesNotSave() {
        //given
        UserNewDto request = mock(UserNewDto.class);

        //when
        when(request.getEmail()).thenReturn("ann@mail.com");
        when(repository.existsByEmail("ann@mail.com")).thenReturn(true);

        //then
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(DuplicateCreationException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void ok_deletesEntity() {
        //given
        UserEntity e = entity(1L);

        //when
        when(repository.findById(1L)).thenReturn(Optional.of(e));

        //then
        userService.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void user_notFound_throws() {
        //given


        //when
        when(repository.findById(1L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(UserNotFoundException.class);
        verify(repository, never()).delete((UserEntity) any());
    }

    @Test
    void ok_updatesEntityViaMapper() {
        //given
        UserEntity e = entity(1L);
        UpdateUserRequest request = mock(UpdateUserRequest.class);

        //when
        when(repository.findById(1L)).thenReturn(Optional.of(e));
        when(userMapper.userToResponseDto(e)).thenReturn(dto(1L));

        //then
        assertThat(userService.update(1L, request)).isEqualTo(dto(1L));
        verify(userMapper).updateEntity(request, e);
    }

    @Test
    void userNotFound_throws() {
        //given


        //when
        when(repository.findById(1L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> userService.update(1L, mock(UpdateUserRequest.class)))
                .isInstanceOf(UserNotFoundException.class);
    }
}