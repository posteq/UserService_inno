package com.innowise.academy.userservice.service;

import com.innowise.academy.userservice.config.UserCacheEvictor;
import com.innowise.academy.userservice.dto.request.card.CardNewDto;
import com.innowise.academy.userservice.dto.request.card.UpdateCardDto;
import com.innowise.academy.userservice.dto.response.card.CardResponseDto;
import com.innowise.academy.userservice.dto.response.card.CardShortResponseDto;
import com.innowise.academy.userservice.entity.PaymentCardEntity;
import com.innowise.academy.userservice.entity.UserEntity;
import com.innowise.academy.userservice.exception.CardExistException;
import com.innowise.academy.userservice.exception.CardLimitExceededException;
import com.innowise.academy.userservice.exception.CardNotFoundException;
import com.innowise.academy.userservice.exception.UserNotFoundException;
import com.innowise.academy.userservice.mapper.CardMapper;
import com.innowise.academy.userservice.repository.PaymentCardRepository;
import com.innowise.academy.userservice.repository.UserRepository;
import com.innowise.academy.userservice.repository.specification.CardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.innowise.academy.userservice.config.RedisCacheConfig.USERS_CACHE;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardMapper cardMapper;
    private final PaymentCardRepository cardRepository;
    private final UserRepository userRepository;

    private final UserCacheEvictor userCacheEvictor;

    static final int MAX_ACTIVE_CARDS = 5;

    @Transactional(readOnly = true)
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
        if(cardRepository.changeActiveById(id,active) == 0){
            throw new CardNotFoundException(id);
        }
    }

    @CacheEvict(cacheNames = USERS_CACHE, key = "#userId")
    public void addCardToUsers(Long userId,CardNewDto card){
        UserEntity user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() ->new UserNotFoundException(userId));

        if (cardRepository.findByNumber(card.getNumber())) {
            throw new CardExistException(card.getNumber());
        }

        long cardsCount = cardRepository.countByUser_IdAndActiveTrue(userId);

        if (cardsCount >= MAX_ACTIVE_CARDS){
            throw new CardLimitExceededException(userId);
        }

        PaymentCardEntity paymentCardEntity = cardMapper.cardNewToEntity(card);
        paymentCardEntity.setUser(user);
        cardRepository.save(paymentCardEntity);
    }

    public void delete(Long id){
        PaymentCardEntity card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        Long userId = card.getUser().getId();
        cardRepository.delete(card);

        userCacheEvictor.evict(userId);
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCard(Long id){
        PaymentCardEntity paymentCardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        return cardMapper.entityToResponseDto(paymentCardEntity);
    }

    @CacheEvict(cacheNames = USERS_CACHE,key = "#result.user().id()")
    public CardResponseDto update(Long id, UpdateCardDto card){
        PaymentCardEntity paymentCardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        cardMapper.updateEntity(card,paymentCardEntity);
        PaymentCardEntity updatedCard = cardRepository.save(paymentCardEntity);

        return cardMapper.entityToResponseDto(updatedCard);
    }

    @Transactional(readOnly = true)
    public List<CardShortResponseDto> getCardsByUserId(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<PaymentCardEntity> allByUserId = cardRepository.getAllByUser_Id(userId);

        return allByUserId.stream()
                .map(cardMapper::entityToShortResponseDto)
                .toList();
    }



}
