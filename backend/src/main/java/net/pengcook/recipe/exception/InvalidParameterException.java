package net.pengcook.recipe.exception;

import org.springframework.http.HttpStatus;

public class InvalidParameterException extends RecipeException {

    public InvalidParameterException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
