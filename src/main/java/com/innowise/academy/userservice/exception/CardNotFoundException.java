package com.innowise.academy.userservice.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(Long id) {
        super("Payment card not found, id = " + id);
    }
}
