package net.pengcook.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final String title;

    public DomainException(String title, String message) {
        super(message);
        this.title = title;
    }
}
