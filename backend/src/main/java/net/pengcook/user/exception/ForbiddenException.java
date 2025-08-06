package net.pengcook.user.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends UserException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
