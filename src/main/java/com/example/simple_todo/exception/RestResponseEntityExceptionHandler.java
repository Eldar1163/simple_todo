package com.example.simple_todo.exception;

import com.example.simple_todo.dto.ErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Validated
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handle(ConstraintViolationException e) {
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadImageFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto BadRequestHandler(RuntimeException exception) {
        return new ErrorDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {ImageServiceException.class, BadImageServerResponseException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorDto imageServiceExceptionHandler(ImageServiceException exception) {
        return new ErrorDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorDto invalidPasswordHandler(InvalidPasswordException exception) {
        return new ErrorDto(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorDto notFoundHandler(NotFoundException exception) {
        return new ErrorDto(exception.getMessage());
    }
}
