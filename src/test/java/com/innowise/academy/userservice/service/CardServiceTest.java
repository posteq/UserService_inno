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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void ok_savesActiveCard() {
        //given
        CardNewDto request = mock(CardNewDto.class);
        PaymentCardEntity mapped = new PaymentCardEntity();
        mapped.setActive(true);
        UserEntity owner = user();

        //when
        when(request.getNumber()).thenReturn(1111L);
        when(userRepository.findByIdAndActiveTrue(USER_ID)).thenReturn(Optional.of(owner));
        when(cardRepository.existsByNumber(1111L)).thenReturn(false);
        when(cardRepository.countByUser_IdAndActiveTrue(USER_ID)).thenReturn(3L);
        when(cardMapper.cardNewToEntity(request)).thenReturn(mapped);

        //then
        cardService.addCardToUsers(USER_ID, request);
        assertThat(mapped.getUser()).isSameAs(owner);
        assertThat(mapped.getActive()).isTrue();
        verify(cardRepository).save(mapped);
    }

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
        //given
        CardNewDto request = mock(CardNewDto.class);

        //when
        when(request.getNumber()).thenReturn(1111L);
        when(userRepository.findByIdAndActiveTrue(USER_ID)).thenReturn(Optional.of(user()));
        when(cardRepository.existsByNumber(1111L)).thenReturn(true);

        //then
        assertThatThrownBy(() -> cardService.addCardToUsers(USER_ID, request))
                .isInstanceOf(CardExistException.class);
        verify(cardRepository, never()).save(any());
        verifyNoInteractions(evictor);
    }

    @ParameterizedTest
    @ValueSource(longs = {5, 6, 100})
    void activeCardsAtOrAboveLimit_throws(long activeCards) {
        //given
        CardNewDto request = mock(CardNewDto.class);

        //when
        when(request.getNumber()).thenReturn(1111L);
        when(userRepository.findByIdAndActiveTrue(USER_ID)).thenReturn(Optional.of(user()));
        when(cardRepository.existsByNumber(1111L)).thenReturn(false);
        when(cardRepository.countByUser_IdAndActiveTrue(USER_ID)).thenReturn(activeCards);

        //then
        assertThatThrownBy(() -> cardService.addCardToUsers(USER_ID, request))
                .isInstanceOf(CardLimitExceededException.class);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void notFound_throws_whenFindByEmail() {
        //given


        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> cardService.changeStatus(CARD_ID, true))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void activate_underLimit_updatesAndEvicts() {
        //given


        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card(false)));
        when(cardRepository.countByUser_IdAndActiveTrue(USER_ID)).thenReturn(4L);

        //then
        cardService.changeStatus(CARD_ID, true);
        verify(cardRepository).changeActiveById(CARD_ID, true);
        verify(evictor).evict(USER_ID);
    }

    @Test
    void activate_atLimit_throws() {
        //given


        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card(false)));
        when(cardRepository.countByUser_IdAndActiveTrue(USER_ID)).thenReturn(5L);

        //then
        assertThatThrownBy(() -> cardService.changeStatus(CARD_ID, true))
                .isInstanceOf(CardLimitExceededException.class);
        verify(cardRepository, never()).changeActiveById(anyLong(), anyBoolean());
    }

    @Test
    void deactivate_neverChecksLimit() {
        //given


        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(card(true)));

        //then
        cardService.changeStatus(CARD_ID, false);
        verify(cardRepository, never()).countByUser_IdAndActiveTrue(anyLong());
        verify(cardRepository).changeActiveById(CARD_ID, false);
        verify(evictor).evict(USER_ID);
    }

    @Test
    void ok_deletesAndEvictsOwnersCache() {
        //given
        PaymentCardEntity c = card(true);

        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(c));

        //then
        cardService.delete(CARD_ID);

        verify(cardRepository).delete(c);
        verify(evictor).evict(USER_ID);
    }

    @Test
    void notFound_throws_when_delete() {
        //given


        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> cardService.delete(CARD_ID))
                .isInstanceOf(CardNotFoundException.class);
        verifyNoInteractions(evictor);
    }

    @Test
    void found_returnsDto() {
        //given
        PaymentCardEntity c = card(true);

        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(c));
        when(cardMapper.entityToResponseDto(c)).thenReturn(responseDto());

        //then
        assertThat(cardService.getCard(CARD_ID)).isEqualTo(responseDto());
    }

    @Test
    void notFound_throws_when_find() {
        //given


        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> cardService.getCard(CARD_ID)).isInstanceOf(CardNotFoundException.class);
    }


    @Test
    void ok_mapsAndEvicts() {
        //given
        PaymentCardEntity c = card(true);
        UpdateCardDto request = mock(UpdateCardDto.class);

        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(c));
        when(cardMapper.entityToResponseDto(c)).thenReturn(responseDto());

        //then
        assertThat(cardService.update(CARD_ID, request)).isEqualTo(responseDto());
        verify(cardMapper).updateEntity(request, c);
    }

    @Test
    void notFound_throws_when_update() {
        //given


        //when
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> cardService.update(CARD_ID, mock(UpdateCardDto.class)))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void getCards_returnsMappedPage() {
        //given
        PaymentCardEntity c = card(true);

        //when
        when(cardRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(c)));
        when(cardMapper.entityToResponseDto(c)).thenReturn(responseDto());

        //then
        Page<CardResponseDto> result = cardService.getCards("ANN", 0, 10, "holder", "ASC");
        assertThat(result.getContent()).containsExactly(responseDto());
    }

    @Test
    void getCards_invalidSort_throws() {
        assertThatThrownBy(() -> cardService.getCards(null, 0, 10, "kkk", "ASC"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getCardsByUserId_mapsAll() {
        //given
        PaymentCardEntity c = card(true);
        CardShortResponseDto shortDto = new CardShortResponseDto(CARD_ID, 1111L, "ANN LEE", EXP_DATE);

        //when
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
        when(cardRepository.getAllByUser_Id(USER_ID)).thenReturn(List.of(c));
        when(cardMapper.entityToShortResponseDto(c)).thenReturn(shortDto);

        //then
        assertThat(cardService.getCardsByUserId(USER_ID)).containsExactly(shortDto);
    }
}