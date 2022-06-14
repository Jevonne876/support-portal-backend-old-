package com.example.demo.exception.domain;

public class NotAnImageFileException extends Exception {
    public NotAnImageFileException(String message) {
        super(message);
    }
}
