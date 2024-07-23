package net.pengcook.category.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class CategoryException extends DomainException {

    public CategoryException(HttpStatus httpStatus, String title, String message) {
        super(httpStatus, title, message);
    }
}
