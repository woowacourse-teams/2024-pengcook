package net.pengcook.authentication.exception;

import org.springframework.http.HttpStatus;

public class FirebaseTokenException extends AuthenticationException {

    public FirebaseTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
