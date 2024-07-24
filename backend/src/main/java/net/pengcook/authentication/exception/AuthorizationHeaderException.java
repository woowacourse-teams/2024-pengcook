package net.pengcook.authentication.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationHeaderException extends AuthenticationException {

    public AuthorizationHeaderException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
