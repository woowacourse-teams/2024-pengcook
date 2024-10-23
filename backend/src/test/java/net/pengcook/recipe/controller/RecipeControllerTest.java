package net.pengcook.recipe.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.recipe.dto.AuthorResponse;
import net.pengcook.recipe.dto.CategoryResponse;
import net.pengcook.recipe.dto.IngredientResponse;
import net.pengcook.recipe.dto.RecipeDescriptionResponse;
import net.pengcook.recipe.dto.RecipeHomeWithMineResponse;
import net.pengcook.recipe.dto.RecipeHomeWithMineResponseV1;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@WithLoginUserTest
@Sql(value = "/data/recipe.sql")
class RecipeControllerTest extends RestDocsSetting {

    private static final int INITIAL_RECIPE_COUNT = 15;

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피 개요 목록을 조회한다.")
    void readRecipes() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 페이지의 레시피 목록을 조회합니다.",
                        "레시피 조회 API",
                        queryParameters(
                                parameterWithName("pageNumber").description("페이지 번호"),
                                parameterWithName("pageSize").description("페이지 크기"),
                                parameterWithName("category").description("조회 카테고리").optional(),
                                parameterWithName("keyword").description("제목 또는 설명 검색 키워드").optional(),
                                parameterWithName("userId").description("작성자 아이디").optional()
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
                                fieldWithPath("[].commentCount").description("댓글 수"),
                                fieldWithPath("[].description").description("레시피 설명"),
                                fieldWithPath("[].createdAt").description("레시피 생성일시"),
                                fieldWithPath("[].category").description("카테고리 목록"),
                                fieldWithPath("[].category[].categoryId").description("카테고리 아이디"),
                                fieldWithPath("[].category[].categoryName").description("카테고리 이름"),
                                fieldWithPath("[].ingredient").description("재료 목록"),
                                fieldWithPath("[].ingredient[].ingredientId").description("재료 아이디"),
                                fieldWithPath("[].ingredient[].ingredientName").description("재료 이름"),
                                fieldWithPath("[].ingredient[].requirement").description("재료 필수 여부"),
                                fieldWithPath("[].mine").description("조회자 작성여부")
                        )))
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .when()
                .get("/recipes")
                .then().log().all()
                .body("size()", is(3))
                .extract()
                .jsonPath()
                .getList(".", RecipeHomeWithMineResponse.class);
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피 개요 목록을 조회한다. V1")
    void readRecipesV1() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 페이지의 레시피 목록을 조회합니다.",
                        "레시피 조회 API",
                        queryParameters(
                                parameterWithName("pageNumber").description("페이지 번호"),
                                parameterWithName("pageSize").description("페이지 크기"),
                                parameterWithName("category").description("조회 카테고리").optional(),
                                parameterWithName("keyword").description("제목 또는 설명 검색 키워드").optional(),
                                parameterWithName("userId").description("작성자 아이디").optional()
                        ),
                        responseFields(
                                fieldWithPath("[]").description("레시피 목록"),
                                fieldWithPath("[].recipeId").description("레시피 아이디"),
                                fieldWithPath("[].title").description("레시피 제목"),
                                fieldWithPath("[].author").description("작성자 정보"),
                                fieldWithPath("[].author.authorId").description("작성자 아이디"),
                                fieldWithPath("[].author.authorName").description("작성자 이름"),
                                fieldWithPath("[].author.authorImage").description("작성자 이미지"),
                                fieldWithPath("[].thumbnail").description("썸네일 이미지"),
                                fieldWithPath("[].likeCount").description("좋아요 수"),
                                fieldWithPath("[].commentCount").description("댓글 수"),
                                fieldWithPath("[].createdAt").description("레시피 생성일시"),
                                fieldWithPath("[].mine").description("조회자 작성여부")
                        )))
                .accept("application/vnd.pengcook.v1+json")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .when()
                .get("/recipes")
                .then().log().all()
                .body("size()", is(3))
                .extract()
                .jsonPath()
                .getList(".", RecipeHomeWithMineResponseV1.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "invalid", " ", "1.2"})
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피 조회 시 페이지 번호가 0 이상의 정수가 아니면 예외가 발생한다.")
    void readRecipesWhenInvalidPageNumber(String pageNumber) {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        queryParameters(
                                parameterWithName("pageNumber").description("페이지 번호"),
                                parameterWithName("pageSize").description("페이지 크기")
                        )))
                .queryParam("pageNumber", pageNumber)
                .queryParam("pageSize", 3)
                .when()
                .get("/recipes")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "0", "invalid", " ", "1.2"})
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피 조회 시 페이지 사이즈가 1 이상의 정수가 아니면 예외가 발생한다.")
    void readRecipesWhenInvalidPageSize(String pageSize) {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        queryParameters(
                                parameterWithName("pageNumber").description("페이지 번호"),
                                parameterWithName("pageSize").description("페이지 크기")
                        )))
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", pageSize)
                .when()
                .get("/recipes")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Sql({"/data/recipe.sql", "/data/like.sql"})
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("내가 좋아요한 레시피 개요 목록을 조회한다.")
    void readLikeRecipes() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "내가 좋아요한 레시피 목록을 조회합니다.",
                        "좋아요한 레시피 조회 API",
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
                                fieldWithPath("[].commentCount").description("댓글 수"),
                                fieldWithPath("[].description").description("레시피 설명"),
                                fieldWithPath("[].createdAt").description("레시피 생성일시"),
                                fieldWithPath("[].category").description("카테고리 목록"),
                                fieldWithPath("[].category[].categoryId").description("카테고리 아이디"),
                                fieldWithPath("[].category[].categoryName").description("카테고리 이름"),
                                fieldWithPath("[].ingredient").description("재료 목록"),
                                fieldWithPath("[].ingredient[].ingredientId").description("재료 아이디"),
                                fieldWithPath("[].ingredient[].ingredientName").description("재료 이름"),
                                fieldWithPath("[].ingredient[].requirement").description("재료 필수 여부"),
                                fieldWithPath("[].mine").description("조회자 작성여부")
                        )))
                .when()
                .get("/recipes/likes")
                .then().log().all()
                .body("size()", is(1))
                .extract()
                .jsonPath()
                .getList(".", RecipeHomeWithMineResponse.class);
    }

    @Test
    @Sql({"/data/recipe.sql", "/data/like.sql"})
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("내가 좋아요한 레시피 개요 목록을 조회한다. V1")
    void readLikeRecipesV1() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "내가 좋아요한 레시피 목록을 조회합니다.",
                        "좋아요한 레시피 조회 API",
                        responseFields(
                                fieldWithPath("[]").description("레시피 목록"),
                                fieldWithPath("[].recipeId").description("레시피 아이디"),
                                fieldWithPath("[].title").description("레시피 제목"),
                                fieldWithPath("[].author").description("작성자 정보"),
                                fieldWithPath("[].author.authorId").description("작성자 아이디"),
                                fieldWithPath("[].author.authorName").description("작성자 이름"),
                                fieldWithPath("[].author.authorImage").description("작성자 이미지"),
                                fieldWithPath("[].thumbnail").description("썸네일 이미지"),
                                fieldWithPath("[].likeCount").description("좋아요 수"),
                                fieldWithPath("[].commentCount").description("댓글 수"),
                                fieldWithPath("[].createdAt").description("레시피 생성일시"),
                                fieldWithPath("[].mine").description("조회자 작성여부")
                        )))
                .accept("application/vnd.pengcook.v1+json")
                .when()
                .get("/recipes/likes")
                .then().log().all()
                .body("size()", is(1))
                .extract()
                .jsonPath()
                .getList(".", RecipeHomeWithMineResponseV1.class);
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
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest("스텝1 이미지.jpg", "스텝1 설명", 1, "00:10:00"),
                new RecipeStepRequest(null, "스텝2 설명", 2, "00:20:00")
        );
        RecipeRequest recipeRequest = new RecipeRequest(
                "새로운 레시피 제목",
                "00:30:00",
                "레시피 썸네일.jpg",
                4,
                "새로운 레시피 설명",
                categories,
                ingredients,
                recipeStepRequests
        );

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "새로운 레시피를 등록합니다.",
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
                                fieldWithPath("ingredients[].substitutions").description("대체 재료 목록").optional(),
                                fieldWithPath("recipeSteps[].image").description("레시피 스텝 이미지").optional(),
                                fieldWithPath("recipeSteps[].description").description("레시피 스텝 설명"),
                                fieldWithPath("recipeSteps[].sequence").description("레시피 스텝 순서"),
                                fieldWithPath("recipeSteps[].cookingTime").description("레시피 스텝 소요시간")
                        ), responseFields(
                                fieldWithPath("recipeId").description("생성된 레시피 아이디")
                        )))
                .contentType(ContentType.JSON)
                .body(recipeRequest)
                .when().post("/recipes")
                .then().log().all()
                .statusCode(201)
                .body("recipeId", is(INITIAL_RECIPE_COUNT + 1));
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피를 수정한다.")
    void updateRecipe() {
        long recipeId = 1L;
        List<String> categories = List.of("Dessert", "NewCategory");
        List<String> substitutions = List.of("Water", "Orange");
        List<IngredientCreateRequest> ingredients = List.of(
                new IngredientCreateRequest("Apple", Requirement.REQUIRED, substitutions),
                new IngredientCreateRequest("WaterMelon", Requirement.OPTIONAL, null)
        );
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest("변경된 스텝 이미지 1.jpg", "변경된 스텝 설명 1", 1, "00:10:00"),
                new RecipeStepRequest("변경된 스텝 이미지 2.jpg", "변경된 스텝 설명 2", 2, "00:20:00")
        );
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest(
                "변경된 레시피 제목",
                "00:30:00",
                "변경된 썸네일.jpg",
                4,
                "변경된 레시피 설명",
                categories,
                ingredients,
                recipeStepRequests
        );

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "레시피를 수정합니다.",
                        "레시피 수정 API",
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
                                fieldWithPath("ingredients[].substitutions").description("대체 재료 목록").optional(),
                                fieldWithPath("recipeSteps[].image").description("레시피 스텝 이미지").optional(),
                                fieldWithPath("recipeSteps[].description").description("레시피 스텝 설명"),
                                fieldWithPath("recipeSteps[].sequence").description("레시피 스텝 순서"),
                                fieldWithPath("recipeSteps[].cookingTime").description("레시피 스텝 소요시간")
                        )))
                .contentType(ContentType.JSON)
                .body(recipeUpdateRequest)
                .when().put("/recipes/{recipeId}", recipeId)
                .then().log().all()
                .statusCode(200);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 11})
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("새로운 레시피 생성 시 올바르지 않은 필드 값을 입력하면 예외가 발생한다.")
    void createRecipeWhenInvalidValue(int difficulty) {
        List<String> categories = List.of("Dessert", "NewCategory");
        List<String> substitutions = List.of("Water", "Orange");
        List<IngredientCreateRequest> ingredients = List.of(
                new IngredientCreateRequest("Apple", Requirement.REQUIRED, substitutions),
                new IngredientCreateRequest("WaterMelon", Requirement.OPTIONAL, null)
        );
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest("스텝1 이미지.jpg", "스텝1 설명", 1, "00:10:00"),
                new RecipeStepRequest(null, "스텝2 설명", 2, "00:20:00")
        );
        RecipeRequest recipeRequest = new RecipeRequest(
                "새로운 레시피 제목",
                "00:30:00",
                "레시피 썸네일.jpg",
                difficulty,
                "새로운 레시피 설명",
                categories,
                ingredients,
                recipeStepRequests
        );

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
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
                                fieldWithPath("ingredients[].substitutions").description("대체 재료 목록").optional(),
                                fieldWithPath("recipeSteps[].image").description("레시피 스텝 이미지").optional(),
                                fieldWithPath("recipeSteps[].description").description("레시피 스텝 설명"),
                                fieldWithPath("recipeSteps[].sequence").description("레시피 스텝 순서"),
                                fieldWithPath("recipeSteps[].cookingTime").description("레시피 스텝 소요시간")
                        )))
                .contentType(ContentType.JSON)
                .body(recipeRequest)
                .when().post("/recipes")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("레시피 상세 스텝을 조회한다.")
    void readRecipeSteps() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 레시피의 상세 스텝을 조회합니다.",
                        "레시피 상세 스텝 조회 API",
                        pathParameters(
                                parameterWithName("recipeId").description("레시피 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("레시피 스텝 목록"),
                                fieldWithPath("[].id").description("레시피 스텝 아이디"),
                                fieldWithPath("[].recipeId").description("레시피 아이디"),
                                fieldWithPath("[].image").description("레시피 스텝 이미지"),
                                fieldWithPath("[].description").description("레시피 스텝 설명"),
                                fieldWithPath("[].sequence").description("레시피 스텝 순서"),
                                fieldWithPath("[].cookingTime").description("레시피 스텝 소요시간")
                        )))
                .when()
                .get("/recipes/{recipeId}/steps", 1L)
                .then().log().all()
                .body("size()", is(3));
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("카테고리별 레시피 개요 목록을 조회한다.")
    void readRecipesOfCategory() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH))
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .queryParam("category", "한식")
                .when().get("/recipes")
                .then().log().all()
                .body("size()", is(3));
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("키워드로 레시피 개요 목록을 조회한다.")
    void readRecipesOfKeyword() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH))
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .queryParam("keyword", "찌개")
                .when().get("/recipes")
                .then().log().all()
                .body("size()", is(2))
                .body("[0].title", is("된장찌개"))
                .body("[1].title", is("김치찌개"));
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("사용자별 레시피 개요 목록을 조회한다.")
    void readRecipesOfUser() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH))
                .queryParam("pageNumber", 1)
                .queryParam("pageSize", 3)
                .queryParam("userId", 1L)
                .when().get("/recipes")
                .then().log().all()
                .body("size()", is(3))
                .body("[0].author.authorId", is(1))
                .body("[1].author.authorId", is(1))
                .body("[2].author.authorId", is(1));
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피 설명을 조회한다.")
    void readRecipeDescription() {
        RecipeDescriptionResponse expected = new RecipeDescriptionResponse(2L, "김밥",
                new AuthorResponse(1L, "loki", "loki.jpg"), LocalTime.of(1, 0, 0),
                "김밥이미지.jpg", 8, 1, 0, "김밥 조리법", LocalDateTime.of(2024, 7, 2, 13, 0, 0),
                List.of(new CategoryResponse(1, "한식"), new CategoryResponse(3, "채식")),
                List.of(new IngredientResponse(2, "쌀", Requirement.REQUIRED),
                        new IngredientResponse(3, "계란", Requirement.OPTIONAL),
                        new IngredientResponse(4, "김치", Requirement.REQUIRED)),
                true);

        RecipeDescriptionResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "특정 아이디의 레시피 설명을 조회합니다.",
                        "레시피 설명 조회 API",
                        pathParameters(
                                parameterWithName("recipeId").description("레시피 아이디")
                        ),
                        responseFields(
                                fieldWithPath("recipeId").description("레시피 아이디"),
                                fieldWithPath("title").description("레시피 제목"),
                                fieldWithPath("author").description("작성자 정보"),
                                fieldWithPath("author.authorId").description("작성자 아이디"),
                                fieldWithPath("author.authorName").description("작성자 이름"),
                                fieldWithPath("author.authorImage").description("작성자 이미지"),
                                fieldWithPath("cookingTime").description("조리 시간"),
                                fieldWithPath("thumbnail").description("썸네일 이미지"),
                                fieldWithPath("difficulty").description("난이도"),
                                fieldWithPath("likeCount").description("좋아요 수"),
                                fieldWithPath("commentCount").description("댓글 수"),
                                fieldWithPath("description").description("레시피 설명"),
                                fieldWithPath("createdAt").description("레시피 생성일시"),
                                fieldWithPath("category").description("카테고리 목록"),
                                fieldWithPath("category[].categoryId").description("카테고리 아이디"),
                                fieldWithPath("category[].categoryName").description("카테고리 이름"),
                                fieldWithPath("ingredient").description("재료 목록"),
                                fieldWithPath("ingredient[].ingredientId").description("재료 아이디"),
                                fieldWithPath("ingredient[].ingredientName").description("재료 이름"),
                                fieldWithPath("ingredient[].requirement").description("재료 필수 여부"),
                                fieldWithPath("mine").description("조회자 작성여부")
                        )))
                .when()
                .get("/recipes/{recipeId}", 2L)
                .then().log().all()
                .extract().as(RecipeDescriptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피를 삭제한다.")
    void deleteRecipe() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "레시피를 삭제합니다.",
                        "레시피 삭제 API",
                        pathParameters(
                                parameterWithName("recipeId").description("레시피 아이디")
                        )))
                .when()
                .delete("/recipes/{recipeId}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @WithLoginUser(email = "ela@pengcook.net")
    @DisplayName("본인이 작성하지 않은 레시피 삭제를 시도하면 예외가 발생한다.")
    void deleteRecipeWhenNonAuthor() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        pathParameters(
                                parameterWithName("recipeId").description("레시피 아이디")
                        )))
                .when()
                .delete("/recipes/{recipeId}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
