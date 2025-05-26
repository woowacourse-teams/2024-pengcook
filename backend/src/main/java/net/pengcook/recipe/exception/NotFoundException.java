package net.pengcook.recipe.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RecipeException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
