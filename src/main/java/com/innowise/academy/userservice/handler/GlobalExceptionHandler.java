package com.innowise.academy.userservice.handler;

import com.innowise.academy.userservice.exception.CardLimitExceededException;
import com.innowise.academy.userservice.exception.DuplicateCreationException;
import com.innowise.academy.userservice.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DuplicateCreationException.class, CardLimitExceededException.class,
                        CardLimitExceededException.class})
    public ResponseEntity<ErrorMessage> handleException(RuntimeException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({ClassNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFound(RuntimeException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }
}
