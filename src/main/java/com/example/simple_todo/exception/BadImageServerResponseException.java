package com.example.simple_todo.exception;

public class BadImageServerResponseException extends RuntimeException{
    public BadImageServerResponseException(String message) {
        super(message);
    }
}
