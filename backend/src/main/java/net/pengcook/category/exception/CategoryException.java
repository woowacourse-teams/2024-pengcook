package net.pengcook.category.exception;

import net.pengcook.exception.DomainException;

public class CategoryException extends DomainException {

    public CategoryException(String title, String message) {
        super(title, message);
    }
}
