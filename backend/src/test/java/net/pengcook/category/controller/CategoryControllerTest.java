package net.pengcook.category.controller;

import io.restassured.RestAssured;
import net.pengcook.RestDocsSetting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

@Sql(value = "/data/category.sql")
class CategoryControllerTest extends RestDocsSetting {

    @Test
    @DisplayName("레시피 개요 목록을 조회한다.")
    void readRecipes() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        requestFields(
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("pageNumber").description("페이지 번호"),
                                fieldWithPath("pageSize").description("페이지 크기")
                        )))
                .queryParam("category", "한식")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .when().get("/api/categories")
                .then().log().all()
                .body("size()", is(3));
    }
}
