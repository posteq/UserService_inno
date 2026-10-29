package com.innowise.academy.userservice.exception;

public class DuplicateCreationException extends RuntimeException{
    public DuplicateCreationException(String email) {
        super("User with current email : " + email + " ,already exist");
    }
}
