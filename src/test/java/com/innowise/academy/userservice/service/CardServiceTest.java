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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock
    private CardMapper cardMapper;
    @Mock
    private PaymentCardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCacheEvictor evictor;

    @InjectMocks
    private CardService cardService;

    private static final Long USER_ID = 10L;
    private static final Long CARD_ID = 1L;
    private static final LocalDate EXP_DATE = LocalDate.of(2099, 1, 1);

    private static UserEntity user() {
        UserEntity u = new UserEntity();
        u.setId(USER_ID);
        u.setActive(true);
        return u;
    }

    private static PaymentCardEntity card(boolean active) {
        PaymentCardEntity c = new PaymentCardEntity();
        c.setId(CARD_ID);
        c.setUser(user());
        c.setNumber(1111L);
        c.setExpirationDate(EXP_DATE);
        c.setActive(active);
        return c;
    }

    private static CardResponseDto responseDto() {
        return new CardResponseDto(CARD_ID, USER_ID, 1111L, "ANN LEE",
                EXP_DATE);
    }

    
//    @Test
//    void ok_savesActiveCardAndEvictsCache() {
//        //given
//        CardNewDto request = mock(CardNewDto.class);
//        PaymentCardEntity mapped = new PaymentCardEntity();
//        UserEntity owner = user();
//
//        //when
//        when(request.getNumber()).thenReturn(1111L);
//        when(userRepository.findByIdAndActiveTrue(USER_ID)).thenReturn(Optional.of(owner));
//        when(cardRepository.findByNumber(1111L)).thenReturn(false);
//        when(cardRepository.countByUser_IdAndActiveTrue(USER_ID)).thenReturn(3L);
//        when(cardMapper.cardNewToEntity(request)).thenReturn(mapped);
//
//        //then
//        cardService.addCardToUsers(USER_ID, request);
//        assertThat(mapped.getUser()).isSameAs(owner);
////        assertThat(mapped.getActive()).isTrue();
//        verify(cardRepository).save(mapped);
//        verify(evictor).evict(USER_ID);
//    }

    @Test
    void userNotFound_throws() {
        //given


        //when
        when(userRepository.findByIdAndActiveTrue(USER_ID)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> cardService.addCardToUsers(USER_ID, mock(CardNewDto.class)))
            .isInstanceOf(UserNotFoundException.class);
        verifyNoInteractions(cardRepository, evictor);
    }

    @Test
    void duplicateNumber_throws() {
        CardNewDto request = mock(CardNewDto.class);
        when(request.getNumber()).thenReturn(1111L);
        when(userRepository.findByIdAndActiveTrue(USER_ID)).thenReturn(Optional.of(user()));
        when(cardRepository.findByNumber(1111L)).thenReturn(true);

        assertThatThrownBy(() -> cardService.addCardToUsers(USER_ID, request))
                .isInstanceOf(CardExistException.class);
        verify(cardRepository, never()).save(any());
        verifyNoInteractions(evictor);
    }

    @ParameterizedTest
    @ValueSource(longs = {5, 6, 100})
    void activeCardsAtOrAboveLimit_throws(long activeCards) {
        CardNewDto request = mock(CardNewDto.class);
        when(request.getNumber()).thenReturn(1111L);
        when(userRepository.findByIdAndActiveTrue(USER_ID)).thenReturn(Optional.of(user()));
        when(cardRepository.findByNumber(1111L)).thenReturn(false);
        when(cardRepository.countByUser_IdAndActiveTrue(USER_ID)).thenReturn(activeCards);

        assertThatThrownBy(() -> cardService.addCardToUsers(USER_ID, request))
                .isInstanceOf(CardLimitExceededException.class);
        verify(cardRepository, never()).save(any());
    }

//    @Test
//    void notFound_throws_when_find_by_email() {
//        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> cardService.changeStatus(CARD_ID, true))
//                .isInstanceOf(CardNotFoundException.class);
//    }

//    @Test
//    void activate_underLimit_updatesAndEvicts() {
//        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card(false)));
//        when(cardRepository.countByUser_IdAndActiveTrue(USER_ID)).thenReturn(4L);
//
//        cardService.changeStatus(CARD_ID, true);
//
//        verify(cardRepository).changeActiveById(CARD_ID, true);
//        verify(evictor).evict(USER_ID);
//    }

//    @Test
//    void activate_atLimit_throws() {
//        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card(false)));
//        when(cardRepository.countByUser_IdAndActiveTrue(USER_ID)).thenReturn(5L);
//
//        assertThatThrownBy(() -> cardService.changeStatus(CARD_ID, true))
//                .isInstanceOf(CardLimitExceededException.class);
//        verify(cardRepository, never()).changeActiveById(anyLong(), anyBoolean());
//    }
//
//    @Test
//    void deactivate_neverChecksLimit() {
//        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card(true)));
//
//        cardService.changeStatus(CARD_ID, false);
//
//        verify(cardRepository, never()).countByUser_IdAndActiveTrue(anyLong());
//        verify(cardRepository).changeActiveById(CARD_ID, false);
//        verify(evictor).evict(USER_ID);
//    }

    @Test
    void ok_deletesAndEvictsOwnersCache() {
        PaymentCardEntity c = card(true);
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(c));

        cardService.delete(CARD_ID);

        verify(cardRepository).delete(c);
        verify(evictor).evict(USER_ID);
    }

    @Test
    void notFound_throws_when_delete() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.delete(CARD_ID))
                .isInstanceOf(CardNotFoundException.class);
        verifyNoInteractions(evictor);
    }

    @Test
    void found_returnsDto() {
        PaymentCardEntity c = card(true);
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(c));
        when(cardMapper.entityToResponseDto(c)).thenReturn(responseDto());

        assertThat(cardService.getCard(CARD_ID)).isEqualTo(responseDto());
    }

    @Test
    void notFound_throws_when_find() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.getCard(CARD_ID)).isInstanceOf(CardNotFoundException.class);
    }


    @Test
    void ok_mapsAndEvicts() {
        PaymentCardEntity c = card(true);
        UpdateCardDto request = mock(UpdateCardDto.class);
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(c));
        when(cardMapper.entityToResponseDto(c)).thenReturn(responseDto());

        assertThat(cardService.update(CARD_ID, request)).isEqualTo(responseDto());
        verify(cardMapper).updateEntity(request, c);
        verify(evictor).evict(USER_ID);
    }

    @Test
    void notFound_throws_when_update() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.update(CARD_ID, mock(UpdateCardDto.class)))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void getCards_returnsMappedPage() {
        PaymentCardEntity c = card(true);
        when(cardRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new Page.of(c));
        when(cardMapper.entityToResponseDto(c)).thenReturn(responseDto());

        PageResponse<CardResponseDto> result = cardService.getCards("ANN", 0, 10, "holder", "ASC");

        assertThat(result.content()).containsExactly(responseDto());
    }

    @Test
    void getCards_invalidSort_throws() {
        assertThatThrownBy(() -> cardService.getCards(null, 0, 10, "kkk", "ASC"))
                .isInstanceOf(IllegalArgumentException.class);
    }

//    @Test
//    void getCardsByUserId_mapsAll() {
//        PaymentCardEntity c = card(true);
//        CardShortResponseDto shortDto = new CardShortResponseDto(CARD_ID, 1111L, "ANN LEE", LocalDate.of(2099, 1, 1));
//        when(cardRepository.getAllByUser_Id(USER_ID)).thenReturn(List.of(c));
//        when(cardMapper.entityToShortResponseDto(c)).thenReturn(shortDto);
//
//        assertThat(cardService.getCardsByUserId(USER_ID)).containsExactly(shortDto);
//    }
}