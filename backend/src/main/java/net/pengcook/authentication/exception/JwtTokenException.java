package net.pengcook.authentication.exception;

import org.springframework.http.HttpStatus;

public class JwtTokenException extends AuthenticationException {

    public JwtTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
