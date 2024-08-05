package net.pengcook.like.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class RecipeLikeException extends DomainException {

    public RecipeLikeException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
