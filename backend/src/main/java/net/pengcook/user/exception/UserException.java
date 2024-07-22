package net.pengcook.user.exception;

import net.pengcook.exception.DomainException;

public class UserException extends DomainException {

    public UserException(String title, String message) {
        super(title, message);
    }
}
