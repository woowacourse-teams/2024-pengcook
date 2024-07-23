package net.pengcook.authentication.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends DomainException {

    public AuthenticationException(HttpStatus httpStatus, String title, String message) {
        super(httpStatus, title, message);
    }
}
