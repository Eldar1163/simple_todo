package com.example.simple_todo.exception;

public class BadImageFileException extends RuntimeException{
    public BadImageFileException(String message) {
        super(message);
    }
}
