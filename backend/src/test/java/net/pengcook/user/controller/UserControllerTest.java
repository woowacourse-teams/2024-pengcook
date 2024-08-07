package net.pengcook.user.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import net.pengcook.user.dto.UpdateProfileRequest;
import net.pengcook.user.dto.UpdateProfileResponse;
import net.pengcook.user.dto.UserBlockRequest;
import net.pengcook.user.dto.UserReportRequest;
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
                                fieldWithPath("region").description("사용자 국가")
                        )
                ))
                .contentType(ContentType.JSON)
                .when().get("/user/me")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);
    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("로그인된 사용자의 정보를 수정한다.")
    void updateUserProfile() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "loki_changed",
                "로키_changed",
                "loki_changed.jpg",
                "KOREA",
                "hello world"
        );

        UpdateProfileResponse expected = new UpdateProfileResponse(
                1L,
                "loki@pengcook.net",
                "loki_changed",
                "로키_changed",
                "loki_changed.jpg",
                "KOREA",
                "hello world"
        );

        UpdateProfileResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "로그인된 사용자 정보를 변경합니다.",
                        "사용자 정보 수정 API",
                        requestFields(
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("image").description("사용자 프로필 이미지"),
                                fieldWithPath("region").description("사용자 국가"),
                                fieldWithPath("introduction").description("사용자 소개")
                        ),
                        responseFields(
                                fieldWithPath("userId").description("사용자 ID"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("image").description("사용자 프로필 이미지"),
                                fieldWithPath("region").description("사용자 국가"),
                                fieldWithPath("introduction").description("사용자 소개")
                        )
                ))
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .patch("/user/me")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UpdateProfileResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("username이 중복되지 않으면 사용 가능하다.")
    void checkUsername() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "username이 중복되면 사용 불가능하다.",
                        "사용자 이름 중복 체크 API",
                        responseFields(
                                fieldWithPath("available").description("사용 가능 여부")
                        )
                ))
                .contentType(ContentType.JSON)
                .queryParam("username", "new_face")
                .when().get("/user/username/check")
                .then().log().all()
                .statusCode(200)
                .body("available", is(true));
    }

    @Test
    @DisplayName("username이 중복되면 사용 불가능하다.")
    void checkUsernameWhenDuplicateUsername() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "username이 중복되면 사용 불가능하다.",
                        "사용자 이름 중복 체크 API",
                        queryParameters(
                                parameterWithName("username").description("사용자 이름")
                        ),
                        responseFields(
                                fieldWithPath("available").description("사용 가능 여부")
                        )
                ))
                .contentType(ContentType.JSON)
                .queryParam("username", "loki")
                .when().get("/user/username/check")
                .then().log().all()
                .statusCode(200)
                .body("available", is(false));
    }

    @Test
    @WithLoginUser
    @DisplayName("유저를 신고한다.")
    void report() {
        UserReportRequest spamReportRequest = new UserReportRequest(
                1,
                "SPAM",
                "스팸 컨텐츠입니다."
        );

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "유저를 신고한다.",
                        "유저 신고 API",
                        requestFields(
                                fieldWithPath("reporteeId").description("피신고자 id"),
                                fieldWithPath("reason").description("사유"),
                                fieldWithPath("details").description("내용")
                        ),
                        responseFields(
                                fieldWithPath("reportId").description("신고 id"),
                                fieldWithPath("reporterId").description("신고자 id"),
                                fieldWithPath("reporteeId").description("피신고자 id"),
                                fieldWithPath("reason").description("사유"),
                                fieldWithPath("details").description("내용"),
                                fieldWithPath("createdAt").description("신고 일자")
                        )))
                .contentType(ContentType.JSON)
                .when()
                .body(spamReportRequest)
                .post("/user/report")
                .then().log().all()
                .statusCode(201)
                .body("reportId", is(1))
                .body("reporterId", is(9))
                .body("reporteeId", is(1))
                .body("reason", is("SPAM"))
                .body("details", is("스팸 컨텐츠입니다."));
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("사용자를 차단한다.")
    void blockUser() {
        UserBlockRequest userBlockRequest = new UserBlockRequest(2L);

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "사용자를 차단합니다.",
                        "사용자 차단 API",
                        requestFields(
                                fieldWithPath("blockeeId").description("차단할 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("blocker.id").description("차단자 ID"),
                                fieldWithPath("blocker.email").description("차단자 이메일"),
                                fieldWithPath("blocker.username").description("차단자 아이디"),
                                fieldWithPath("blocker.nickname").description("차단자 닉네임"),
                                fieldWithPath("blocker.image").description("차단자 프로필 이미지"),
                                fieldWithPath("blocker.region").description("차단자 국가"),
                                fieldWithPath("blockee.id").description("차단대상 ID"),
                                fieldWithPath("blockee.email").description("차단대상 이메일"),
                                fieldWithPath("blockee.username").description("차단대상 아이디"),
                                fieldWithPath("blockee.nickname").description("차단대상 닉네임"),
                                fieldWithPath("blockee.image").description("차단대상 프로필 이미지"),
                                fieldWithPath("blockee.region").description("차단대상 국가")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(userBlockRequest)
                .when().post("/user/block")
                .then().log().all()
                .statusCode(201)
                .body("blocker.id", is(1))
                .body("blockee.id", is(2));
    }
}
