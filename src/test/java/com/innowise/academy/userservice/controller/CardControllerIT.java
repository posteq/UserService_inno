package com.innowise.academy.userservice.controller;


import com.innowise.academy.userservice.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CardControllerIT extends BaseIT {

    private ResultActions addCard(Long userId, Long number) throws Exception {
        return mockMvc.perform(post("/api/users/{id}/cards", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cardJson(number)));
    }

    @Test
    void addCard_returns201_andCardIsLinkedToUser() throws Exception {
        UserEntity user = saveUser("Ann", "ann@mail.com");

        addCard(user.getId(), CARD_NUMBER).andExpect(status().isCreated());

        mockMvc.perform(get("/api/users/{id}/cards", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].number").value(CARD_NUMBER));
    }

    @Test
    void addCard_sixthActiveCard_returns409() throws Exception {
        UserEntity user = saveUser("Ann", "ann@mail.com");
        for (long n = 1; n <= 5; n++) {
            addCard(user.getId(), CARD_NUMBER + n).andExpect(status().isCreated());
        }

        addCard(user.getId(), 2000000000000000L).andExpect(status().isConflict());

        assertThat(cardRepository.count()).isEqualTo(5);
    }

    @Test
    void addCard_inactiveCardsDoNotCountTowardsLimit() throws Exception {
        UserEntity user = saveUser("Ann", "ann@mail.com");
        for (long n = 1; n <= 5; n++) {
            addCard(user.getId(), CARD_NUMBER + n).andExpect(status().isCreated());
        }
        Long firstCardId = cardRepository.findAll().get(0).getId();
        cardService.changeStatus(firstCardId, false);

        addCard(user.getId(), 2000000000000000L).andExpect(status().isCreated());
    }

    @Test
    void addCard_duplicateNumber_returns409() throws Exception {
        UserEntity ann = saveUser("Ann", "ann@mail.com");
        UserEntity bob = saveUser("Bob", "bob@mail.com");
        addCard(ann.getId(), CARD_NUMBER).andExpect(status().isCreated());

        addCard(bob.getId(), CARD_NUMBER).andExpect(status().isConflict());
    }

    @Test
    void addCard_unknownUser_returns404() throws Exception {
        addCard(999_999L, CARD_NUMBER).andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000-01-01", "2020-06-15"})
    void addCard_expirationInThePast_returns400(String expiration) throws Exception {
        UserEntity user = saveUser("Ann", "ann@mail.com");
        String body = """
                {"number": 1111111111111111, "holder": "ANN LEE", "expirationDate": "%s"}
                """.formatted(expiration);

        mockMvc.perform(post("/api/users/{id}/cards", user.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCard_updateCard_deleteCard_fullFlow() throws Exception {
        UserEntity user = saveUser("Ann", "ann@mail.com");
        addCard(user.getId(), CARD_NUMBER).andExpect(status().isCreated());
        Long cardId = cardRepository.findAll().getFirst().getId();

        mockMvc.perform(get("/api/cards/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.holder").value("ANN LEE"));

        mockMvc.perform(put("/api/cards/{id}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"holder": "ANNA LEE", "expirationDate": "2099-12-31"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("ANNA LEE"));
        assertThat(cardRepository.findById(cardId).orElseThrow().getHolder()).isEqualTo("ANNA LEE");

        mockMvc.perform(delete("/api/cards/{id}", cardId)).andExpect(status().isNoContent());
        assertThat(cardRepository.existsById(cardId)).isFalse();
    }

    @Test
    void getCard_unknown_returns404() throws Exception {
        mockMvc.perform(get("/api/cards/{id}", 999_999L)).andExpect(status().isNotFound());
    }

    @Test
    void deleteCard_unknown_returns404() throws Exception {
        mockMvc.perform(delete("/api/cards/{id}", 999_999L)).andExpect(status().isNotFound());
    }
}