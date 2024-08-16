package net.pengcook.comment.dto;

import java.time.LocalDateTime;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.domain.Comment;

public record CommentOfRecipeResponse(
        Long commentId,
        Long userId,
        String userImage,
        String userName,
        LocalDateTime createdAt,
        String message,
        boolean mine
) {

    public CommentOfRecipeResponse(Comment comment, UserInfo userInfo) {
        this(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getImage(),
                comment.getUser().getUsername(),
                comment.getCreatedAt(),
                comment.getMessage(),
                userInfo.isSameUser(comment.getUser().getId())
        );
    }
}
