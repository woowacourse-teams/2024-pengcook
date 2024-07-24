package net.pengcook.category.controller;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(value = "/data/category.sql")
class CategoryControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("레시피 개요 목록을 조회한다.")
    void readRecipes() {
        RestAssured.given().log().all()
                .queryParam("category", "한식")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .when()
                .get("/api/categories")
                .then().log().all()
                .body("size()", is(3));
    }
}
