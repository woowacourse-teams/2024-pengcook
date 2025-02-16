package net.pengcook.user.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import java.util.List;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;


@WithLoginUserTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/data/blocked-user.sql")
class BlockedUserFilterAspectTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피 목록을 조회할때 차단한 사용자들의 레시피가 보이지 않는다.")
    void filterOwnables() {
        List<Long> authorIds = RestAssured.given().log().all()
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .when()
                .get("/recipes")
                .then().log().all()
                .extract().jsonPath()
                .getList("author.authorId", Long.class);

        assertThat(authorIds).doesNotContain(2L, 4L, 6L, 8L);
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("레시피를 조회할때 내가 차단한 사용자의 레시피가 보이지 않는다.")
    void filterBlockeeOwnable() {
        RestAssured.given().log().all()
                .when()
                .get("/recipes/{id}", 2L)
                .then().log().all()
                .statusCode(403)
                .body("detail", equalTo("내가 차단한 사용자의 게시글을 이용할 수 없습니다."));
    }

    @Test
    @WithLoginUser(email = "ela@pengcook.net")
    @DisplayName("레시피를 조회할때 나를 차단한 사용자의 레시피가 보이지 않는다.")
    void filterBlockerOwnable() {
        RestAssured.given().log().all()
                .when()
                .get("/recipes/{id}", 1L)
                .then().log().all()
                .statusCode(403)
                .body("detail", equalTo("나를 차단한 사용자의 게시글을 이용할 수 없습니다."));
    }
}
