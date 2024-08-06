package net.pengcook.user.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends UserException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
