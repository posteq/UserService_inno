package com.innowise.academy.userservice.repository;

import com.innowise.academy.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserEntity c SET c.active =:active WHERE c.id =:id")
    void changeActiveById(Long id, Boolean active);

    Optional<UserEntity> findByIdAndActive(Long id, Boolean active);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "paymentCards")
    Optional<UserEntity> findWithCardsById(Long id);
}

