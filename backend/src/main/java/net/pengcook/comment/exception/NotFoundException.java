package net.pengcook.comment.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CommentException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
