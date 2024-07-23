package net.pengcook.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DomainException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String title;

    public DomainException(HttpStatus httpStatus, String title, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.title = title;
    }
}
