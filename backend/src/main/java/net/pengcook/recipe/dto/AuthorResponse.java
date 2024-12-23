package net.pengcook.recipe.dto;

import net.pengcook.user.domain.User;

public record AuthorResponse(long authorId, String authorName, String authorImage) {

    public AuthorResponse(User author) {
        this(author.getId(), author.getUsername(), author.getImage());
    }
}
