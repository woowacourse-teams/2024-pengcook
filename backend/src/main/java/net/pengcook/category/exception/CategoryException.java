package net.pengcook.category.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class CategoryException extends DomainException {

    public CategoryException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
