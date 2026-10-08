package com.innowise.academy.userservice.repository;

import com.innowise.academy.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity> {

    @Modifying
    @Query("UPDATE UserEntity c SET c.active =: active WHERE c.id =: id")
    void changeActiveById(Long id, Boolean active);
}

