package net.pengcook.like.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql("/data/like.sql")
@WithLoginUserTest
class RecipeLikeControllerTest extends RestDocsSetting {

    @Test
    @WithLoginUser(email = "ela@pengcook.net")
    @DisplayName("게시글의 좋아요 여부를 조회한다.")
    void readLike() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 레시피의 좋아요 여부를 조회합니다.",
                        "레시피별 좋아요 여부 조회 API",
                        pathParameters(
                                parameterWithName("recipeId").description("레시피 아이디")
                        ),
                        responseFields(
                                fieldWithPath("isLike").description("나의 좋아요 여부")
                        )))
                .when().get("/likes/{recipeId}", 2L)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("isLike", is(true));
    }

    @Test
    @WithLoginUser(email = "ela@pengcook.net")
    @DisplayName("게시글에 좋아요를 등록한다.")
    void addLike() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 레시피의 좋아요를 변경합니다.",
                        "레시피별 좋아요 변경 API",
                        requestFields(
                                fieldWithPath("recipeId").description("레시피 아이디"),
                                fieldWithPath("isLike").description("나의 좋아요 여부")
                        )))
                .body(Map.of("recipeId", 3L, "isLike", true))
                .contentType(ContentType.JSON)
                .when().post("/likes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @WithLoginUser(email = "ela@pengcook.net")
    @DisplayName("게시글에 좋아요를 취소한다.")
    void deleteLike() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 레시피의 좋아요를 변경합니다.",
                        "레시피별 좋아요 변경 API",
                        requestFields(
                                fieldWithPath("recipeId").description("레시피 아이디"),
                                fieldWithPath("isLike").description("좋아요 여부")
                        )))
                .body(Map.of("recipeId", 2L, "isLike", false))
                .contentType(ContentType.JSON)
                .when().post("/likes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
