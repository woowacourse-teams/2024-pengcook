package net.pengcook.recipe.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import java.time.LocalTime;
import java.util.List;
import net.pengcook.recipe.dto.RecipeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
class RecipeControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("id로 레시피를 조회한다.")
    void readRecipe() {
        RecipeResponse expected = new RecipeResponse(
                "Steamed Tofu with Shrimp and Eggs",
                "http://www.foodsafetykorea.go.kr/uploadimg/cook/10_00028_2.png",
                "https://avatars.githubusercontent.com/u/22692687?v=4",
                "Seyang",
                List.of("Korean food"),
                List.of("Soft Tofu", "Cocktail Shrimp", "Egg", "Sugar", "Unsalted Butter"),
                List.of("Heavy Cream", "Spinach"),
                5,
                LocalTime.parse("00:30:00"),
                1534,
                36056,
                "This is steamed Tofu with Shrimp and Eggs."
        );

        RecipeResponse actual = RestAssured.given().log().all()
                .when().get("/api/recipe/1")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(RecipeResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 페이지 레시피들을 조회한다.")
    void readHomeRecipe() {
        HomeRecipeRequest requestBody = new HomeRecipeRequest(1, 10);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().get("/api/home")
                .then().log().all()
                .statusCode(200)
                .body("content.size()", is(10));
    }
}
