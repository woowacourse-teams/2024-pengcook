package net.pengcook.comment.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class CommentException extends DomainException {

    public CommentException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
