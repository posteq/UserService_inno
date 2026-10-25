package com.innowise.academy.userservice.handler;

import com.innowise.academy.userservice.exception.CardExistException;
import com.innowise.academy.userservice.exception.CardLimitExceededException;
import com.innowise.academy.userservice.exception.CardNotFoundException;
import com.innowise.academy.userservice.exception.DuplicateCreationException;
import com.innowise.academy.userservice.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DuplicateCreationException.class, CardLimitExceededException.class,
                        CardExistException.class})
    public ResponseEntity<ErrorMessage> handleException(RuntimeException e){
        return ResponseEntity.status(CONFLICT)
                .body(new ErrorMessage(CONFLICT,e.getMessage()));
    }

    @ExceptionHandler({CardNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFound(RuntimeException e){
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorMessage(NOT_FOUND,e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,IllegalArgumentException.class})
    public ResponseEntity<ErrorMessage> handleNotValid(Exception e){
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorMessage(BAD_REQUEST,e.getMessage()));
    }
}
