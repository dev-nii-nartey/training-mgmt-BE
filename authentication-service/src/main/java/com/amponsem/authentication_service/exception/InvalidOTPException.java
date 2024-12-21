package com.amponsem.authentication_service.exception;

public class InvalidOTPException extends RuntimeException {
        public InvalidOTPException(String message) {
            super(message);
        }
    }

