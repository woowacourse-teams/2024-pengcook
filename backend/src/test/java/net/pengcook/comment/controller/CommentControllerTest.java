package net.pengcook.comment.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import io.restassured.RestAssured;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@WithLoginUserTest
@Sql(value = "/data/comment.sql")
class CommentControllerTest extends RestDocsSetting {

    @Test
    @WithLoginUser(email = "ela@pengcook.net")
    @DisplayName("레시피의 댓글을 조회한다.")
    void readComments() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 레시피의 댓글을 조회합니다.",
                        "레시피별 댓글 조회 API",
                        pathParameters(
                                parameterWithName("recipeId").description("레시피 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("댓글 목록"),
                                fieldWithPath("[].commentId").description("댓글 아이디"),
                                fieldWithPath("[].userId").description("작성자 아이디"),
                                fieldWithPath("[].userImage").description("작성자 이미지"),
                                fieldWithPath("[].userName").description("작성자 이름"),
                                fieldWithPath("[].createdAt").description("작성 시간"),
                                fieldWithPath("[].message").description("내용"),
                                fieldWithPath("[].mine").description("소유 여부")
                        )))
                .when().get("/api/comments/{recipeId}", 1L)
                .then().log().all()
                .body("size()", is(2));
    }
}
