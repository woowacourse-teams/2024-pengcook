package net.pengcook.like.exception;

import org.springframework.http.HttpStatus;

public class RecipeNotFoundException extends RecipeLikeException {

    public RecipeNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 레시피 입니다.");
    }
}
