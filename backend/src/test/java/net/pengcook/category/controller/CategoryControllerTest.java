package net.pengcook.category.controller;

import io.restassured.RestAssured;
import net.pengcook.RestDocsSetting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

@Sql(value = "/data/category.sql")
class CategoryControllerTest extends RestDocsSetting {

    @Test
    @DisplayName("레시피 개요 목록을 조회한다.")
    void readRecipes() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
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
                .when().get("/api/categories")
                .then().log().all()
                .body("size()", is(3));
    }
}