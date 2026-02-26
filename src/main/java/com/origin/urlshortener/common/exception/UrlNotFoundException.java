package com.origin.urlshortener.common.exception;

public class UrlNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UrlNotFoundException(String code) {
        super("Short URL code not found: " + code);
    }
}
