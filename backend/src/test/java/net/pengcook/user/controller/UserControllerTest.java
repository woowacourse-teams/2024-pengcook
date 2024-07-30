package net.pengcook.user.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import net.pengcook.user.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@WithLoginUserTest
@Sql("/data/users.sql")
class UserControllerTest extends RestDocsSetting {

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("id를 통해 사용자의 정보를 조회한다.")
    void getUserProfile() {
        UserResponse expected = new UserResponse(
                1L,
                "loki@pengcook.net",
                "loki",
                "로키",
                "loki.jpg",
                LocalDate.of(1999, 8, 8),
                "KOREA"
        );

        UserResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "사용자 ID를 통해 사용자 정보를 조회합니다.",
                        "사용자 정보 조회 API",
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("image").description("사용자 프로필 이미지"),
                                fieldWithPath("birth").description("사용자 생년월일"),
                                fieldWithPath("region").description("사용자 국가")
                        )
                ))
                .contentType(ContentType.JSON)
                .when().get("/api/user/me")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
