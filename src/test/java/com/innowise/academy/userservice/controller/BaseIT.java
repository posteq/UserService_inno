package com.innowise.academy.userservice.controller;

import com.innowise.academy.userservice.config.TestContainerConfig;
import com.innowise.academy.userservice.entity.UserEntity;
import com.innowise.academy.userservice.repository.PaymentCardRepository;
import com.innowise.academy.userservice.repository.UserRepository;
import com.innowise.academy.userservice.service.CardService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Objects;

import static com.innowise.academy.userservice.config.RedisCacheConfig.USERS_CACHE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AllArgsConstructor
@NoArgsConstructor
@Import(TestContainerConfig.class)
public class BaseIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PaymentCardRepository cardRepository;
    @Autowired
    CardService cardService;
    @Autowired
    CacheManager manager;

    final static Long CARD_NUMBER = 1111_1111_1111_1111L;

    @BeforeEach
    void cleanState() {
        cardRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        Objects.requireNonNull(manager.getCache(USERS_CACHE)).clear();
    }

    protected UserEntity saveUser(String firstname, String email) {
        UserEntity u = new UserEntity();
        u.setFirstname(firstname);
        u.setSurname("Lee");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        u.setEmail(email);
        u.setActive(true);
        return userRepository.save(u);
    }

    protected static String cardJson(long number) {
        return """
                {"number": %d, "holder": "ANN LEE", "expirationDate": "2099-12-31"}
                """.formatted(number);
    }

    protected static String userJson(String firstname, String email) {
        return """
                {"firstname": "%s", "surname": "Lee", "birthday": "1990-01-01", "email": "%s"}
                """.formatted(firstname, email);
    }
}
