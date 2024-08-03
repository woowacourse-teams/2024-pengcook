package net.pengcook.comment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.domain.Comment;
import net.pengcook.comment.dto.CommentResponse;
import net.pengcook.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<CommentResponse> readComments(Long recipeId, UserInfo userInfo) {
        List<Comment> comments = commentRepository.findByRecipeId(recipeId);

        return comments.stream()
                .map(comment -> new CommentResponse(comment, userInfo))
                .toList();
    }
}
