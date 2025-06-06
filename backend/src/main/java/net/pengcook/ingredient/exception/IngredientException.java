package net.pengcook.ingredient.exception;

import net.pengcook.exception.DomainException;
import org.springframework.http.HttpStatus;

public class IngredientException extends DomainException {

    public IngredientException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
