package net.pengcook.ingredient.exception;

import org.springframework.http.HttpStatus;

public class InvalidNameException extends IngredientException {

    public InvalidNameException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
