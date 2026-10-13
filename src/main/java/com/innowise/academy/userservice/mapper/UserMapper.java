package com.innowise.academy.userservice.mapper;

import com.innowise.academy.userservice.dto.request.user.UpdateUserRequest;
import com.innowise.academy.userservice.dto.request.user.UserNewDto;
import com.innowise.academy.userservice.dto.response.user.UserResponseDto;
import com.innowise.academy.userservice.dto.response.user.UserResponseDtoWithCards;
import com.innowise.academy.userservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE,
        uses = {CardMapper.class})
public interface UserMapper {

    @Mapping(source = "paymentCards",target = "cards")
    UserResponseDtoWithCards userToResponseDtoWithCards(UserEntity entity);

    UserResponseDto userToResponseDto(UserEntity entity);

    @Mapping(target = "active", constant = "true")
    UserEntity userNewDtoToUser(UserNewDto user);

    void updateEntity(UpdateUserRequest request, @MappingTarget UserEntity user);
}
