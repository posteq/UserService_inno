package com.innowise.academy.userservice.exception;

public class CardLimitExceededException extends RuntimeException{
    public CardLimitExceededException(Long userId) {
        super("This with  id = " + userId + " cannot have more than 5 active cards");
    }
}
