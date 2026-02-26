package com.origin.urlshortener.util.exception;

public class UrlNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE_TEMPLATE = "Short URL code not found: %s";

    public UrlNotFoundException(String code) {
        super(formatMessage(code));
    }

    public UrlNotFoundException(String code, Throwable cause) {
        super(formatMessage(code), cause);
    }

    public UrlNotFoundException(String message, String code, Throwable cause) {
        super(message != null ? message : formatMessage(code), cause);
    }

    private static String formatMessage(String code) {
        return String.format(MESSAGE_TEMPLATE, code);
    }
}
