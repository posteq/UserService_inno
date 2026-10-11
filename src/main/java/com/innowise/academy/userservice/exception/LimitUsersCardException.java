package com.innowise.academy.userservice.exception;

public class LimitUsersCardException extends RuntimeException{
    public LimitUsersCardException(Long userId) {
        super("This with  id = " + userId + " cannot have more than 5 active cards");
    }
}
