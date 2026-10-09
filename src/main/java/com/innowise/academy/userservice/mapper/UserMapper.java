package com.innowise.academy.userservice.mapper;

import com.innowise.academy.userservice.dto.user.UpdateUserRequest;
import com.innowise.academy.userservice.dto.user.UserNewDto;
import com.innowise.academy.userservice.dto.user.UserResponseDto;
import com.innowise.academy.userservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE)
public interface UserMapper {

    @Mapping(source = "paymentsCards",target = "cards")
    UserResponseDto userToResponseDto(UserEntity entity);

    @Mapping(source = "cards",target = "paymentsCards")
    UserEntity responseDtoToEntity(UserResponseDto response);

    UserEntity userNewDtoToUser(UserNewDto user);

    void updateEntity(UpdateUserRequest request, @MappingTarget UserEntity user);
}
