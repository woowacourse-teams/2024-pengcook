package net.pengcook.follow.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
