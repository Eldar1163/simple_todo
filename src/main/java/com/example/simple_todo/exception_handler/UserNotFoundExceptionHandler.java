package com.example.simple_todo.exception_handler;

import com.example.simple_todo.dto.ErrorDto;
import com.example.simple_todo.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UserNotFoundExceptionHandler {
    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorDto userNotFoundHandler(UserNotFoundException exception) {
        return new ErrorDto(exception.getMessage());
    }
}
