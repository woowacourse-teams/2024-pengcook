package net.pengcook.category.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import net.pengcook.RestDocsSetting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = "/data/category.sql")
class CategoryControllerTest extends RestDocsSetting {

    @Test
    @DisplayName("레시피 개요 목록을 조회한다.")
    void readRecipes() {
        RestAssured.given(spec).log().all()
                .filter(document("카테고리 목록 조회 API"))
                .queryParam("category", "한식")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .when()
                .get("/api/categories")
                .then().log().all()
                .body("size()", is(3));
    }
}
