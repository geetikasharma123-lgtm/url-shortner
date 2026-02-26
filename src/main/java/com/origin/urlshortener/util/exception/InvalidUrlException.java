package com.origin.urlshortener.util.exception;

public class InvalidUrlException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidUrlException(String message) {
        super(message);
    }
}
