package com.innowise.academy.userservice.repository;

import com.innowise.academy.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserEntity c SET c.active =:active , c.updatedAt = local datetime WHERE c.id =:id")
    int changeActiveById(Long id, Boolean active);

    @Lock(PESSIMISTIC_WRITE)
    Optional<UserEntity> findByIdAndActiveTrue(Long id);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "paymentCards")
    Optional<UserEntity> findWithCardsById(Long id);
}

