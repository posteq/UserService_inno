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

    PaymentCardEntity responseDtoToEntity(CardResponseDto dto);

    CardResponseDto entityToResponseDto(PaymentCardEntity entity);

    CardShortResponseDto entityToShortResponseDto(PaymentCardEntity entity);

    @Mapping(target = "active", constant = "true")
    PaymentCardEntity cardNewToEntity(CardNewDto card);

    void updateEntity(UpdateCardDto request, @MappingTarget PaymentCardEntity card);
}
