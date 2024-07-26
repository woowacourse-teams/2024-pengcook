package net.pengcook.user.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import net.pengcook.authentication.dto.TokenPayload;
import net.pengcook.authentication.util.JwtTokenManager;
import net.pengcook.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("/data/users.sql")
class UserControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    private JwtTokenManager jwtTokenManager;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("id를 통해 사용자의 정보를 조회한다.")
    void getUserById() {
        long id = 1L;
        String accessToken = jwtTokenManager.createToken(new TokenPayload(id, "loki@pengcook.net"));
        UserResponse expected = new UserResponse(
                id,
                "loki@pengcook.net",
                "loki",
                "로키",
                "loki.jpg",
                LocalDate.of(1999, 8, 8),
                "KOREA"
        );

        UserResponse actual = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/api/user/me")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
