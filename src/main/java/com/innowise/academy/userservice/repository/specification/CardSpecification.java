package com.innowise.academy.userservice.repository.specification;

import com.innowise.academy.userservice.entity.PaymentCardEntity;
import org.springframework.data.jpa.domain.Specification;

public final class CardSpecification {

    public static Specification<PaymentCardEntity> hasHolder(String holder){
        return (root,query,cb)->{
            if(holder.isBlank()){
                return cb.conjunction();
            }
            return cb.equal(
                    cb.lower(root.get("holder")),holder.trim().toLowerCase()
            );
        };
    }
}
