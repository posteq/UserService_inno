package com.innowise.academy.userservice.controller;

import com.innowise.academy.userservice.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserControllerTest extends BaseIT{

    @Test
    void createUser_returns201WithLocation_andPersists() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("Ann", "ann@mail.com")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.email").value("ann@mail.com"))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        UserEntity inDb = userRepository.findById(id).orElseThrow();
        assertThat(inDb.getFirstname()).isEqualTo("Ann");
        assertThat(inDb.getCreatedAt()).isNotNull();
    }

    @Test
    void createUser_duplicateEmail_returns409() throws Exception {
        saveUser("Ann", "ann@mail.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("Other", "ann@mail.com")))
                .andExpect(status().isConflict());

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void createUser_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("Ann", "not-email")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsers_filtersByFirstname_andPaginates() throws Exception {
        saveUser("Ann", "a@mail.com");
        saveUser("Bob", "b@mail.com");
        saveUser("Carl", "c@mail.com");

        mockMvc.perform(get("/api/users").param("firstname", "Ann"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].firstname").value("Ann"));

        mockMvc.perform(get("/api/users").param("size", "2").param("sortBy", "firstname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getUsers_unknownSortField_returns400() throws Exception {
        mockMvc.perform(get("/api/users").param("sortBy", "password"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999_999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_changesDataInDb() throws Exception {
        UserEntity user = saveUser("Ann", "ann@mail.com");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson("Anna", "ann2@mail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Anna"));

        assertThat(userRepository.findById(user.getId()).orElseThrow().getFirstname()).isEqualTo("Anna");
    }

    @Test
    void changeStatus_deactivatesUser_andUpdatesTimestamp() throws Exception {
        UserEntity user = saveUser("Ann", "ann@mail.com");

        mockMvc.perform(patch("/api/users/{id}/active", user.getId()).param("active", "false"))
                .andExpect(status().isNoContent());

        UserEntity inDb = userRepository.findById(user.getId()).orElseThrow();
        assertThat(inDb.getActive()).isFalse();
        assertThat(inDb.getUpdatedAt()).isAfterOrEqualTo(inDb.getCreatedAt());
    }

    @Test
    void changeStatus_unknownUser_returns404() throws Exception {
        mockMvc.perform(patch("/api/users/{id}/status", 999_999L).param("active", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_removesUserAndCascadesCards() throws Exception {
        UserEntity user = saveUser("Ann", "ann@mail.com");
        mockMvc.perform(post("/api/users/{id}/cards", user.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(cardJson(CARD_NUMBER)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(user.getId())).isFalse();
        assertThat(cardRepository.count()).isZero();
    }

    @Test
    void deleteUser_unknown_returns404() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 999_999L)).andExpect(status().isNotFound());
    }

}