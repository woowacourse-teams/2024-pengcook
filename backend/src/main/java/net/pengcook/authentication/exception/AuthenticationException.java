package net.pengcook.authentication.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends DomainException {

    public AuthenticationException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
