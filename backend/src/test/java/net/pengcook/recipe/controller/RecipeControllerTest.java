package net.pengcook.recipe.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.recipe.dto.RecipeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@WithLoginUserTest
@Sql(value = "/data/recipe.sql")
class RecipeControllerTest extends RestDocsSetting {

    @Test
    @DisplayName("레시피 개요 목록을 조회한다.")
    void readRecipes() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 페이지의 레시피 목록을 조회합니다.",
                        "레시피 조회 API",
                        queryParameters(
                                parameterWithName("pageNumber").description("페이지 번호"),
                                parameterWithName("pageSize").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("레시피 목록"),
                                fieldWithPath("[].recipeId").description("레시피 아이디"),
                                fieldWithPath("[].title").description("레시피 제목"),
                                fieldWithPath("[].author").description("작성자 정보"),
                                fieldWithPath("[].author.authorId").description("작성자 아이디"),
                                fieldWithPath("[].author.authorName").description("작성자 이름"),
                                fieldWithPath("[].author.authorImage").description("작성자 이미지"),
                                fieldWithPath("[].cookingTime").description("조리 시간"),
                                fieldWithPath("[].thumbnail").description("썸네일 이미지"),
                                fieldWithPath("[].difficulty").description("난이도"),
                                fieldWithPath("[].likeCount").description("좋아요 수"),
                                fieldWithPath("[].description").description("레시피 설명"),
                                fieldWithPath("[].category").description("카테고리 목록"),
                                fieldWithPath("[].category[].categoryId").description("카테고리 아이디"),
                                fieldWithPath("[].category[].categoryName").description("카테고리 이름"),
                                fieldWithPath("[].ingredient").description("재료 목록"),
                                fieldWithPath("[].ingredient[].ingredientId").description("재료 아이디"),
                                fieldWithPath("[].ingredient[].ingredientName").description("재료 이름"),
                                fieldWithPath("[].ingredient[].requirement").description("재료 필수 여부")
                        )))
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .when()
                .get("/api/recipes")
                .then().log().all()
                .body("size()", is(3));
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("새로운 레시피를 생성한다.")
    void createRecipe() {
        List<String> categories = List.of("Dessert", "NewCategory");
        List<String> substitutions = List.of("Water", "Orange");
        List<IngredientCreateRequest> ingredients = List.of(
                new IngredientCreateRequest("Apple", Requirement.REQUIRED, substitutions),
                new IngredientCreateRequest("WaterMelon", Requirement.OPTIONAL, null)
        );
        RecipeRequest recipeRequest = new RecipeRequest(
                "새로운 레시피 제목",
                "00:30:00",
                "레시피 썸네일.jpg",
                4,
                "새로운 레시피 설명",
                categories,
                ingredients
        );

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "새로운 레시피 개요를 등록합니다.",
                        "신규 레시피 생성 API",
                        requestFields(
                                fieldWithPath("title").description("레시피 제목"),
                                fieldWithPath("cookingTime").description("조리 시간"),
                                fieldWithPath("thumbnail").description("썸네일 이미지"),
                                fieldWithPath("difficulty").description("난이도"),
                                fieldWithPath("description").description("레시피 설명"),
                                fieldWithPath("categories").description("카테고리 목록"),
                                fieldWithPath("ingredients[]").description("재료 목록"),
                                fieldWithPath("ingredients[].name").description("재료 이름"),
                                fieldWithPath("ingredients[].requirement").description("재료 필수 여부"),
                                fieldWithPath("ingredients[].substitutions").description("대체 재료 목록").optional()
                        ), responseFields(
                                fieldWithPath("recipeId").description("생성된 레시피 아이디")
                        )))
                .contentType(ContentType.JSON)
                .body(recipeRequest)
                .when().post("/api/recipes")
                .then().log().all()
                .statusCode(201)
                .body("recipeId", is(16));
    }

    @Test
    @DisplayName("레시피 상세 스텝을 조회한다.")
    void readRecipeSteps() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 레시피의 상세 스텝을 조회합니다.",
                        "레시피 상세 스텝 조회 API",
                        pathParameters(
                                parameterWithName("id").description("레시피 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("레시피 스텝 목록"),
                                fieldWithPath("[].id").description("레시피 스텝 아이디"),
                                fieldWithPath("[].recipeId").description("레시피 아이디"),
                                fieldWithPath("[].image").description("레시피 스텝 이미지"),
                                fieldWithPath("[].description").description("레시피 스텝 설명"),
                                fieldWithPath("[].sequence").description("레시피 스텝 순서")
                        )))
                .when()
                .get("/api/recipes/{id}/steps", 1L)
                .then().log().all()
                .body("size()", is(3));
    }

    @Test
    @DisplayName("특정 레시피의 특정 스텝을 조회한다.")
    void readRecipeStep() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 레시피의 특정 스텝을 조회합니다.",
                        "특정 레시피 특정 스텝 조회 API",
                        pathParameters(
                                parameterWithName("recipeId").description("조회할 레시피 아이디"),
                                parameterWithName("sequence").description("조회할 스텝 순서")
                        ),
                        responseFields(
                                fieldWithPath("id").description("레시피 스텝 아이디"),
                                fieldWithPath("recipeId").description("레시피 아이디"),
                                fieldWithPath("image").description("레시피 스텝 이미지"),
                                fieldWithPath("description").description("레시피 스텝 설명"),
                                fieldWithPath("sequence").description("레시피 스텝 순서")
                        )))
                .when()
                .get("/api/recipes/{recipeId}/steps/{sequence}", 1L, 1L)
                .then().log().all()
                .statusCode(200)
                .body("description", is("레시피1 설명1"));
    }

    @Test
    @DisplayName("카테고리별 레시피 개요 목록을 조회한다.")
    void readRecipesOfCategory() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 카테고리 페이지의 레시피 목록을 조회합니다.",
                        "카테고리별 레시피 조회 API",
                        queryParameters(
                                parameterWithName("category").description("카테고리"),
                                parameterWithName("pageNumber").description("페이지 번호"),
                                parameterWithName("pageSize").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("레시피 목록"),
                                fieldWithPath("[].recipeId").description("레시피 아이디"),
                                fieldWithPath("[].title").description("레시피 제목"),
                                fieldWithPath("[].author").description("작성자 정보"),
                                fieldWithPath("[].author.authorId").description("작성자 아이디"),
                                fieldWithPath("[].author.authorName").description("작성자 이름"),
                                fieldWithPath("[].author.authorImage").description("작성자 이미지"),
                                fieldWithPath("[].cookingTime").description("조리 시간"),
                                fieldWithPath("[].thumbnail").description("썸네일 이미지"),
                                fieldWithPath("[].difficulty").description("난이도"),
                                fieldWithPath("[].likeCount").description("좋아요 수"),
                                fieldWithPath("[].description").description("레시피 설명"),
                                fieldWithPath("[].category").description("카테고리 목록"),
                                fieldWithPath("[].category[].categoryId").description("카테고리 아이디"),
                                fieldWithPath("[].category[].categoryName").description("카테고리 이름"),
                                fieldWithPath("[].ingredient").description("재료 목록"),
                                fieldWithPath("[].ingredient[].ingredientId").description("재료 아이디"),
                                fieldWithPath("[].ingredient[].ingredientName").description("재료 이름"),
                                fieldWithPath("[].ingredient[].requirement").description("재료 필수 여부")
                        )))
                .queryParam("category", "한식")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .when().get("/api/recipes/search")
                .then().log().all()
                .body("size()", is(3));
    }
}
