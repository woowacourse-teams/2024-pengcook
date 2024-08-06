package net.pengcook.like.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = "/data/like.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@WithLoginUserTest
class RecipeLikeControllerTest extends RestDocsSetting {

    @Test
    @DisplayName("게시글의 좋아요 개수를 조회한다.")
    void readLikesCount() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        pathParameters(
                                parameterWithName("recipeId").description("레시피 아이디")
                        ),
                        responseFields(
                                fieldWithPath("likesCount").description("좋아요 개수")
                        )))
                .when().get("/api/likes/{recipeId}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("likesCount", is(1));
    }

    @Test
    @WithLoginUser(email = "ela@pengcook.net")
    @DisplayName("게시글에 좋아요 상태를 반대로 변경한다.")
    void toggleLikeOnEmpty() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 레시피의 좋아요를 변경합니다.",
                        "레시피별 좋아요 변경 API",
                        pathParameters(
                                parameterWithName("recipeId").description("레시피 아이디")
                        )))
                .when().post("/api/likes/{recipeId}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
