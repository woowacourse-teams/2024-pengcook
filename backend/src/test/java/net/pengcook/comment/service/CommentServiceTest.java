package net.pengcook.comment.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.dto.CommentResponse;
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

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("레시피의 댓글을 조회한다.")
    void readComments() {
        UserInfo userInfo = new UserInfo(1, "ela@pengcook.net");
        List<CommentResponse> expect = List.of(
                new CommentResponse(1L, 2L, "loki.jpg", "loki", LocalDateTime.of(2024, 1, 1, 0, 0, 0), "great", false),
                new CommentResponse(2L, 1L, "ela.jpg", "ela", LocalDateTime.of(2024, 1, 2, 0, 0, 0), "thank you", true)
        );

        List<CommentResponse> actual = commentService.readComments(1L, userInfo);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
    }
}
