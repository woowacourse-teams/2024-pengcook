package net.pengcook.comment.repository;

import java.util.List;
import net.pengcook.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByRecipeId(long recipeId);
}