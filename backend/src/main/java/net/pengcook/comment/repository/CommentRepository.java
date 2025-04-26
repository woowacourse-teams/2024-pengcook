package net.pengcook.comment.repository;

import java.util.List;
import net.pengcook.block.repository.OwnableRepository;
import net.pengcook.comment.domain.Comment;

public interface CommentRepository extends OwnableRepository<Comment, Long> {

    List<Comment> findAllByRecipeId(long recipeId);

    void deleteByRecipeId(long recipeId);

    void deleteByUserId(long userId);

    List<Comment> findAllByUserId(long userId);
}
