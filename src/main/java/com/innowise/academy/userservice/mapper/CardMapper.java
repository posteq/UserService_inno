package com.innowise.academy.userservice.mapper;

import com.innowise.academy.userservice.dto.request.card.CardNewDto;
import com.innowise.academy.userservice.dto.response.card.CardResponseDto;
import com.innowise.academy.userservice.dto.request.card.UpdateCardDto;
import com.innowise.academy.userservice.dto.response.card.CardShortResponseDto;
import com.innowise.academy.userservice.entity.PaymentCardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE)
public interface CardMapper {

    @Mapping(source = "user.id" , target = "userId")
    CardResponseDto entityToResponseDto(PaymentCardEntity entity);

    CardShortResponseDto entityToShortResponseDto(PaymentCardEntity entity);

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentCardEntity cardNewToEntity(CardNewDto card);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(UpdateCardDto request, @MappingTarget PaymentCardEntity card);
}
