package com.github.devrafaht.userapi.domain.exception;

public class VerificationTimeoutException extends RuntimeException {
    public VerificationTimeoutException(String message) {
        super(message);
    }
}
