package com.example.simple_todo.exception;

import com.example.simple_todo.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidPasswordExceptionHandler {
    @ResponseBody
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorDto invalidPasswordHandler(InvalidPasswordException exception) {
        return new ErrorDto(exception.getMessage());
    }
}
