package com.samdobsondev.lcde4j.exception;

public class SSLContextCreationException extends Exception {
    public SSLContextCreationException(String message) {
        super(message);
    }

    public SSLContextCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
