package com.innowise.academy.userservice.service;

import com.innowise.academy.userservice.dto.card.CardNewDto;
import com.innowise.academy.userservice.dto.card.CardResponseDto;
import com.innowise.academy.userservice.dto.card.UpdateCardDto;
import com.innowise.academy.userservice.entity.PaymentCardEntity;
import com.innowise.academy.userservice.exception.CardNotFoundException;
import com.innowise.academy.userservice.mapper.CardMapper;
import com.innowise.academy.userservice.repository.PaymentCardRepository;
import com.innowise.academy.userservice.repository.specification.CardSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CardService {

    private final CardMapper cardMapper;
    private PaymentCardRepository repository;

    public List<CardResponseDto> getCards(String holder,
                                          int page, int size, String sortBy, String sortDir){
        Specification<PaymentCardEntity> cardSpecification = Specification
                .where(CardSpecification.hasHolder(holder));

        Sort sortOrder = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(page, size, sortOrder);

        return repository.findAll(cardSpecification, pageRequest)
                .getContent().stream()
                .map(cardMapper::entityToResponseDto)
                .toList();
    }

    public void changeStatus(Long id , Boolean active){
        repository.changeActiveById(id,active);
    }

    public CardResponseDto create(CardNewDto card){
        PaymentCardEntity paymentCardEntity = cardMapper.cardNewToEntity(card);
        PaymentCardEntity savedCard = repository.save(paymentCardEntity);

        return cardMapper.entityToResponseDto(savedCard);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }

    public CardResponseDto getCard(Long id){
        PaymentCardEntity paymentCardEntity = repository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        return cardMapper.entityToResponseDto(paymentCardEntity);
    }

    public CardResponseDto update(Long id, UpdateCardDto card){
        PaymentCardEntity paymentCardEntity = repository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        cardMapper.updateEntity(card,paymentCardEntity);

        PaymentCardEntity updatedCard = repository.save(paymentCardEntity);
        return cardMapper.entityToResponseDto(updatedCard);
    }

    public List<CardResponseDto> getCardsByUserId(Long userId){
        List<PaymentCardEntity> allByUserId = repository.getAllByUserId(userId);

        return allByUserId.stream()
                .map(cardMapper::entityToResponseDto)
                .toList();
    }



}
