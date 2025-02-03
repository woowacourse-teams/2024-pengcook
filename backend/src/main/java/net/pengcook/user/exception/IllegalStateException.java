package net.pengcook.user.exception;

import org.springframework.http.HttpStatus;

public class IllegalStateException extends UserException {

    public IllegalStateException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
