package com.innowise.academy.userservice.repository.specification;

import com.innowise.academy.userservice.entity.UserEntity;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
public final class UserSpecification {

        public static Specification<UserEntity> hasFirstname(String firstname){
            return (root,query,cb)->{
              if(firstname == null || firstname.isBlank()){
                  return cb.conjunction();
              }
              return cb.equal(
                      cb.lower(root.get("name")),firstname.trim().toLowerCase()
              );
            };
        }

    public static Specification<UserEntity> hasSurname(String surname){
        return (root,query,cb)->{
            if(surname == null || surname.isBlank()){
                return cb.conjunction();
            }
            return cb.equal(
                    cb.lower(root.get("surname")),surname.trim().toLowerCase()
            );
        };
    }
}
