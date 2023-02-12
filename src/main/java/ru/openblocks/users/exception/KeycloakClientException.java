package ru.openblocks.users.exception;

public class KeycloakClientException extends RuntimeException {

    public KeycloakClientException() {
        super();
    }

    public KeycloakClientException(String message) {
        super(message);
    }
}
