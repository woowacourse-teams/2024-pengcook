package net.pengcook.comment.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.domain.Comment;
import net.pengcook.comment.dto.CommentOfUserResponse;
import net.pengcook.comment.dto.CommentOfRecipeResponse;
import net.pengcook.comment.dto.CreateCommentRequest;
import net.pengcook.comment.exception.NotFoundException;
import net.pengcook.comment.exception.UnauthorizedDeletionException;
import net.pengcook.comment.repository.CommentRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentOfRecipeResponse> readComments(Long recipeId, UserInfo userInfo) {
        List<Comment> comments = commentRepository.findAllByRecipeId(recipeId);

        return comments.stream()
                .map(comment -> new CommentOfRecipeResponse(comment, userInfo))
                .toList();
    }

    @Transactional
    public void createComment(CreateCommentRequest request, UserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new NotFoundException("해당되는 유저가 없습니다."));
        Recipe recipe = recipeRepository.findById(request.recipeId())
                .orElseThrow(() -> new NotFoundException("해당되는 레시피가 없습니다."));

        Comment comment = new Comment(user, recipe, request.message(), LocalDateTime.now());
        commentRepository.save(comment);

        recipe.increaseCommentCount();
    }

    @Transactional
    public void deleteComment(long commentId, UserInfo userInfo) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("해당되는 댓글이 없습니다."));

        if (!isCommentOwner(userInfo, comment)) {
            throw new UnauthorizedDeletionException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);

        Recipe recipe = comment.getRecipe();
        recipe.decreaseCommentCount();
    }

    @Transactional
    public void deleteCommentsByRecipe(long recipeId) {
        commentRepository.deleteByRecipeId(recipeId);
    }

    @Transactional
    public void deleteCommentsByUser(long userId) {
        commentRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<CommentOfUserResponse> readCommentsOfUser(UserInfo userInfo) {
        List<Comment> comments = commentRepository.findAllByUserId(userInfo.getId());

        return comments.stream()
                .map(CommentOfUserResponse::new)
                .toList();
    }

    private boolean isCommentOwner(UserInfo userInfo, Comment comment) {
        User owner = comment.getUser();
        long userId = userInfo.getId();

        return owner.isSameUser(userId);
    }
}
