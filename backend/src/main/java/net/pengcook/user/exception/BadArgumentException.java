package net.pengcook.user.exception;

import org.springframework.http.HttpStatus;

public class BadArgumentException extends UserException {

    public BadArgumentException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
