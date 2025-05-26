package net.pengcook.like.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends RecipeLikeException {

    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 유저 입니다.");
    }
}
