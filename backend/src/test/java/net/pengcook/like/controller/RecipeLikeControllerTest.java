package net.pengcook.like.controller;

import io.restassured.RestAssured;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@Sql("/data/like.sql")
@WithLoginUserTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeLikeControllerTest extends RestDocsSetting {

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
                .then().log().all();
    }
}
