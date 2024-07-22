package net.pengcook.recipe.exception;

import net.pengcook.exception.DomainException;

public class RecipeException extends DomainException {

    public RecipeException(String title, String message) {
        super(title, message);
    }
}
