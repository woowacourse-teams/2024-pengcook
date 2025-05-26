package net.pengcook.recipe.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RecipeException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
