package net.pengcook.user.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class UserException extends DomainException {

    public UserException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
