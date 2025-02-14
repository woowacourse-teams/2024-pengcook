package net.pengcook.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.List;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.dto.CommentOfRecipeResponse;
import net.pengcook.comment.dto.CommentOfUserResponse;
import net.pengcook.comment.dto.CreateCommentRequest;
import net.pengcook.comment.exception.NotFoundException;
import net.pengcook.comment.exception.UnauthorizedDeletionException;
import net.pengcook.comment.repository.CommentRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import(CommentService.class)
@Sql(scripts = "/data/comment.sql")
class CommentServiceTest {

    private static final int INITIAL_TOTAL_COMMENT_COUNT = 4;

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @DisplayName("레시피의 댓글을 조회한다.")
    void readComments() {
        UserInfo userInfo = new UserInfo(1, "ela@pengcook.net");
        List<CommentOfRecipeResponse> expect = List.of(
                new CommentOfRecipeResponse(1L, 2L, "loki.jpg", "loki", LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                        "great", false),
                new CommentOfRecipeResponse(2L, 1L, "ela.jpg", "ela", LocalDateTime.of(2024, 1, 2, 0, 0, 0),
                        "thank you", true),
                new CommentOfRecipeResponse(4L, 3L, "ato.jpg", "ato", LocalDateTime.of(2024, 1, 3, 0, 0, 0),
                        "haha", false)
        );

        List<CommentOfRecipeResponse> actual = commentService.readComments(1L, userInfo);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
    }

    @Test
    @DisplayName("댓글을 등록한다.")
    void createComment() {
        CreateCommentRequest request = new CreateCommentRequest(2L, "thank you!");
        UserInfo userInfo = new UserInfo(2L, "ela@pengcook.net");
        Recipe recipe = recipeRepository.findById(2L).orElseThrow();
        int beforeRecipeCommentCount = recipe.getCommentCount();

        commentService.createComment(request, userInfo);
        int afterRecipeCommentCount = recipeRepository.findById(2L).orElseThrow().getCommentCount();

        assertAll(
                () -> assertThat(commentRepository.count()).isEqualTo(INITIAL_TOTAL_COMMENT_COUNT + 1),
                () -> assertThat(afterRecipeCommentCount).isEqualTo(beforeRecipeCommentCount + 1)
        );
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteComment() {
        UserInfo userInfo = new UserInfo(1L, "ela@pengcook.net");
        Recipe recipe = commentRepository.findById(2L).orElseThrow().getRecipe();
        Long recipeId = recipe.getId();
        int beforeRecipeCommentCount = recipe.getCommentCount();

        commentService.deleteComment(2L, userInfo);
        int afterRecipeCommentCount = recipeRepository.findById(recipeId).orElseThrow().getCommentCount();

        assertAll(
                () -> assertThat(commentRepository.count()).isEqualTo(INITIAL_TOTAL_COMMENT_COUNT - 1),
                () -> assertThat(afterRecipeCommentCount).isEqualTo(beforeRecipeCommentCount - 1)
        );
    }

    @Test
    @DisplayName("존재하지 않는 댓글을 삭제하려고 하면 예외가 발생한다.")
    void deleteCommentWhenNotExistComment() {
        UserInfo userInfo = new UserInfo(1L, "ela@pengcook.net");

        assertThatThrownBy(() -> commentService.deleteComment(1000L, userInfo))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당되는 댓글이 없습니다.");
    }

    @Test
    @DisplayName("본인 댓글이 아닌 댓글을 삭제하려고 하면 예외가 발생한다.")
    void deleteCommentWhenNotCommentOwner() {
        UserInfo userInfo = new UserInfo(1L, "ela@pengcook.net");

        assertThatThrownBy(() -> commentService.deleteComment(1L, userInfo))
                .isInstanceOf(UnauthorizedDeletionException.class)
                .hasMessage("삭제 권한이 없습니다.");
    }

    @Test
    @DisplayName("특정 레시피의 댓글들을 삭제한다.")
    void deleteCommentsByRecipe() {
        commentService.deleteCommentsByRecipe(1L);

        assertThat(commentRepository.count()).isEqualTo(INITIAL_TOTAL_COMMENT_COUNT - 3);
    }

    @Test
    @DisplayName("특정 사용자의 댓글들을 삭제한다.")
    void deleteCommentsByUser() {
        commentService.deleteCommentsByUser(2L);

        assertThat(commentRepository.count()).isEqualTo(INITIAL_TOTAL_COMMENT_COUNT - 2);
    }

    @Test
    @DisplayName("특정 사용자의 댓글을 조회한다.")
    void readCommentsOfUser() {
        UserInfo userInfo = new UserInfo(2, "loki@pengcook.net");
        List<CommentOfUserResponse> expect = List.of(
                new CommentOfUserResponse(3L, 2L, "김치찌개", "김치찌개이미지.jpg", LocalDateTime.of(2024, 5, 5, 0, 0, 0), "good"),
                new CommentOfUserResponse(1L, 1L, "김밥", "김밥이미지.jpg", LocalDateTime.of(2024, 1, 1, 0, 0, 0), "great")
        );

        List<CommentOfUserResponse> actual = commentService.readCommentsOfUserV1(userInfo);

        assertThat(actual).containsExactlyElementsOf(expect);
    }
}
