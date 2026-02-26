package com.origin.urlshortener.util.exception;

public class InvalidUrlException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE =
            "Invalid URL format. Must be absolute http/https URL.";

    public InvalidUrlException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidUrlException(String message) {
        super(message);
    }

    public InvalidUrlException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUrlException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
