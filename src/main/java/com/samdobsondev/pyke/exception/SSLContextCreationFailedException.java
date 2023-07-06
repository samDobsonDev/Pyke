package com.samdobsondev.pyke.exception;

public class SSLContextCreationFailedException extends RuntimeException {
    public SSLContextCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
