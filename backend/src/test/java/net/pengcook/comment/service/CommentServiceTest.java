package net.pengcook.comment.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.dto.CommentOfRecipeResponse;
import net.pengcook.comment.dto.CreateCommentRequest;
import net.pengcook.comment.repository.CommentRepository;
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

    private static final int INITIAL_COMMENT_COUNT = 3;

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("레시피의 댓글을 조회한다.")
    void readComments() {
        UserInfo userInfo = new UserInfo(1, "ela@pengcook.net");
        List<CommentOfRecipeResponse> expect = List.of(
                new CommentOfRecipeResponse(1L, 2L, "loki.jpg", "loki", LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                        "great", false),
                new CommentOfRecipeResponse(2L, 1L, "ela.jpg", "ela", LocalDateTime.of(2024, 1, 2, 0, 0, 0),
                        "thank you", true)
        );

        List<CommentOfRecipeResponse> actual = commentService.readComments(1L, userInfo);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
    }

    @Test
    @DisplayName("댓글을 등록한다.")
    void createComment() {
        CreateCommentRequest request = new CreateCommentRequest(2L, "thank you!");
        UserInfo userInfo = new UserInfo(2L, "ela@pengcook.net");

        commentService.createComment(request, userInfo);

        assertThat(commentRepository.count()).isEqualTo(INITIAL_COMMENT_COUNT + 1);
    }
}
