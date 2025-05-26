package net.pengcook.authentication.exception;

import org.springframework.http.HttpStatus;

public class NoSuchUserException extends AuthenticationException {

    public NoSuchUserException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
