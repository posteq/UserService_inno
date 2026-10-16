package com.innowise.academy.userservice.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long id) {
        super("Invalid id of user , id = " + id);
    }
}
