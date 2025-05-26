package net.pengcook.authentication.exception;

import org.springframework.http.HttpStatus;

public class DuplicationException extends AuthenticationException {

    public DuplicationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
