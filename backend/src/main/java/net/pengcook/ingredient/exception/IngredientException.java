package net.pengcook.ingredient.exception;

import net.pengcook.exception.DomainException;

public class IngredientException extends DomainException {

    public IngredientException(String title, String message) {
        super(title, message);
    }
}
