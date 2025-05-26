package net.pengcook.recipe.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class RecipeException extends DomainException {

    public RecipeException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
