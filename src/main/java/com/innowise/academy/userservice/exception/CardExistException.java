package com.innowise.academy.userservice.exception;

public class CardExistException extends RuntimeException{
    public CardExistException(Long cardNumber) {
        super("This card already exist , number : " + cardNumber);
    }
}
