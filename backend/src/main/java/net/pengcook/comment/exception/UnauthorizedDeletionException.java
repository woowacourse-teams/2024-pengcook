package net.pengcook.comment.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedDeletionException extends CommentException{

    public UnauthorizedDeletionException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
