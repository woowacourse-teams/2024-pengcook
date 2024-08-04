package net.pengcook.comment.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import net.pengcook.comment.dto.CreateCommentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Test
    @WithLoginUser(email = "ela@pengcook.net")
    @DisplayName("댓글을 등록한다.")
    void createComment() {
        CreateCommentRequest request = new CreateCommentRequest(1L, "thank you!");

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "레시피에 댓글을 등록합니다.",
                        "댓글 등록 API",
                        requestFields(
                                fieldWithPath("recipeId").description("레시피 아이디"),
                                fieldWithPath("message").description("댓글 내용")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/comments")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("댓글 등록 시 레시피 아이디에 0을 입력하면 예외가 발생한다.")
    void createCommentWithInvalidRecipeId() {
        CreateCommentRequest request = new CreateCommentRequest(0L, "thank you!");

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "레시피에 댓글을 등록할 때 유효하지 않은 레시피 아이디를 입력하면 예외가 발생합니다.",
                        "댓글 조회 API",
                        requestFields(
                                fieldWithPath("recipeId").description("레시피 아이디"),
                                fieldWithPath("message").description("댓글 내용")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/comments")
                .then().log().all()
                .statusCode(400);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("댓글 등록 시 빈 댓글 내용을 입력하면 예외가 발생한다.")
    void createCommentWithBlankMessage(String message) {
        CreateCommentRequest request = new CreateCommentRequest(1L, message);

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "레시피에 댓글을 등록할 때 유효하지 않은 댓글 내용을 입력하면 예외가 발생합니다.",
                        "댓글 조회 API",
                        requestFields(
                                fieldWithPath("recipeId").description("레시피 아이디"),
                                fieldWithPath("message").description("댓글 내용")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("api/comments")
                .then().log().all()
                .statusCode(400);
    }
}
