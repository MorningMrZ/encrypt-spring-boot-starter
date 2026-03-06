package com.z.ai.encrypt.exception;

/**
 * Exception for encryption/decryption errors
 */
public class EncryptException extends RuntimeException {

    public EncryptException(String message) {
        super(message);
    }

    public EncryptException(String message, Throwable cause) {
        super(message, cause);
    }
}
