package com.innowise.academy.userservice.repository;

import com.innowise.academy.userservice.entity.PaymentCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCardEntity,Long>, JpaSpecificationExecutor<PaymentCardEntity> {
    List<PaymentCardEntity> getAllByUserId(Long id);

    @Modifying
    @Query("UPDATE PaymentCardEntity c SET c.active =: active WHERE c.id =: id")
    void changeActiveById(Long id, Boolean active);

    long countByUser_IdAndActiveTrue(Long userId);
}
