package com.example.sample.api.exception;

/**
 * Indicates that the client submitted an invalid request.
 */
public class InvalidRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidRequestException(final String message) {
        super(message);
    }
}
