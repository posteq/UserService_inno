package com.innowise.academy.userservice.service;

import com.innowise.academy.userservice.dto.card.CardNewDto;
import com.innowise.academy.userservice.dto.card.CardResponseDto;
import com.innowise.academy.userservice.dto.card.UpdateCardDto;
import com.innowise.academy.userservice.entity.PaymentCardEntity;
import com.innowise.academy.userservice.entity.UserEntity;
import com.innowise.academy.userservice.exception.CardNotFoundException;
import com.innowise.academy.userservice.exception.LimitUsersCardException;
import com.innowise.academy.userservice.exception.UserNotFoundException;
import com.innowise.academy.userservice.mapper.CardMapper;
import com.innowise.academy.userservice.repository.PaymentCardRepository;
import com.innowise.academy.userservice.repository.UserRepository;
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
    private PaymentCardRepository cardRepository;
    private UserRepository userRepository;

    public List<CardResponseDto> getCards(String holder,
                                          int page, int size, String sortBy, String sortDir){
        Specification<PaymentCardEntity> cardSpecification = Specification
                .where(CardSpecification.hasHolder(holder));

        Sort sortOrder = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(page, size, sortOrder);

        return cardRepository.findAll(cardSpecification, pageRequest)
                .getContent().stream()
                .map(cardMapper::entityToResponseDto)
                .toList();
    }

    public void changeStatus(Long id , Boolean active){
        cardRepository.changeActiveById(id,active);
    }

    public CardResponseDto addCardToUsers(Long userId,CardNewDto card){
        UserEntity user = userRepository.findByIdAndActive(userId, Boolean.TRUE)
                .orElseThrow(() ->new UserNotFoundException(userId));
        long cardsCount = cardRepository.countByUser_IdAndActiveTrue(userId);
        if (cardsCount >= 5){
            throw new LimitUsersCardException(userId);
        }

        PaymentCardEntity paymentCardEntity = cardMapper.cardNewToEntity(card);
        paymentCardEntity.setUser(user);
        PaymentCardEntity savedCard = cardRepository.save(paymentCardEntity);

        return cardMapper.entityToResponseDto(savedCard);
    }

    public void delete(Long id){
        cardRepository.deleteById(id);
    }

    public CardResponseDto getCard(Long id){
        PaymentCardEntity paymentCardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        return cardMapper.entityToResponseDto(paymentCardEntity);
    }

    public CardResponseDto update(Long id, UpdateCardDto card){
        PaymentCardEntity paymentCardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        cardMapper.updateEntity(card,paymentCardEntity);

        PaymentCardEntity updatedCard = cardRepository.save(paymentCardEntity);
        return cardMapper.entityToResponseDto(updatedCard);
    }

    public List<CardResponseDto> getCardsByUserId(Long userId){
        List<PaymentCardEntity> allByUserId = cardRepository.getAllByUserId(userId);

        return allByUserId.stream()
                .map(cardMapper::entityToResponseDto)
                .toList();
    }



}
