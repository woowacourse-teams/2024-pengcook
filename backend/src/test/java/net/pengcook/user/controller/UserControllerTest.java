package net.pengcook.user.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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
import net.pengcook.user.domain.Reason;
import net.pengcook.user.domain.Type;
import net.pengcook.user.dto.ProfileResponse;
import net.pengcook.user.dto.ReportRequest;
import net.pengcook.user.dto.UpdateProfileRequest;
import net.pengcook.user.dto.UpdateProfileResponse;
import net.pengcook.user.dto.UserBlockRequest;
import net.pengcook.user.dto.UserReportRequest;
import net.pengcook.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@WithLoginUserTest
@Sql("/data/users.sql")
class UserControllerTest extends RestDocsSetting {

    @Autowired
    UserRepository userRepository;

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("로그인된 사용자의 정보를 조회한다.")
    void getUserProfileWithUserInfo() {
        ProfileResponse expected = new ProfileResponse(
                1L,
                "loki@pengcook.net",
                "loki",
                "로키",
                "loki.jpg",
                "KOREA",
                "hello world",
                0L,
                0L,
                15L
        );

        ProfileResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "로그인된 사용자 정보를 조회합니다.",
                        "로그인된 사용자 정보 조회 API",
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("image").description("사용자 프로필 이미지"),
                                fieldWithPath("region").description("사용자 국가"),
                                fieldWithPath("introduction").description("사용자 소개"),
                                fieldWithPath("follower").description("팔로워 수"),
                                fieldWithPath("following").description("팔로잉 수"),
                                fieldWithPath("recipeCount").description("게시한 레시피 수")
                        )
                ))
                .contentType(ContentType.JSON)
                .when().get("/user/me")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProfileResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("userId로 사용자 정보를 확인한다.")
    void getUserProfileWithUserId() {
        ProfileResponse expected = new ProfileResponse(
                1L,
                "loki@pengcook.net",
                "loki",
                "로키",
                "loki.jpg",
                "KOREA",
                "hello world",
                0L,
                0L,
                15L
        );

        ProfileResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "userId로 사용자 정보를 조회합니다.",
                        "사용자 정보 조회 API",
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("image").description("사용자 프로필 이미지"),
                                fieldWithPath("region").description("사용자 국가"),
                                fieldWithPath("introduction").description("사용자 소개"),
                                fieldWithPath("follower").description("팔로워 수"),
                                fieldWithPath("following").description("팔로잉 수"),
                                fieldWithPath("recipeCount").description("게시한 레시피 수")
                        )
                ))
                .contentType(ContentType.JSON)
                .when().get("/user/{userId}", 1L)
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ProfileResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

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
    @DisplayName("username이 중복되는지 확인한다.")
    void checkUsername() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "username이 중복되는지 확인합니다.",
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
    @DisplayName("username이 중복되는지 확인한다.")
    void checkUsernameWhenDuplicateUsername() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "username이 중복되는지 확인합니다.",
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
    @DisplayName("레시피 또는 사용자 또는 댓글을 신고한다.")
    void report() {
        ReportRequest spamReportRequest = new ReportRequest(
                1,
                Reason.SPAM_CONTENT,
                Type.RECIPE,
                1L,
                null
        );

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "레시피 또는 유저 또는 댓글을 신고한다.",
                        "신고 API",
                        requestFields(
                                fieldWithPath("reporteeId").description("피신고자 id"),
                                fieldWithPath("reason").description("신고 사유"),
                                fieldWithPath("type").description("신고 대상 종류"),
                                fieldWithPath("targetId").description("신고 대상 id"),
                                fieldWithPath("details").description("상세 내용")
                        ),
                        responseFields(
                                fieldWithPath("reportId").description("신고 id"),
                                fieldWithPath("reporterId").description("신고자 id"),
                                fieldWithPath("reporteeId").description("피신고자 id"),
                                fieldWithPath("reason").description("신고 사유"),
                                fieldWithPath("type").description("신고 대상 종류"),
                                fieldWithPath("targetId").description("신고 대상 id"),
                                fieldWithPath("details").description("상세 내용"),
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
                .body("reason", is(Reason.SPAM_CONTENT.name()))
                .body("type", is(Type.RECIPE.name()))
                .body("targetId", is(1))
                .body("details", nullValue());
    }

    @Test
    @DisplayName("신고 사유 목록을 조회한다.")
    void getReportReasons() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "신고 사유 목록을 조회합니다.",
                        "신고 사유 목록 조회 API",
                        responseFields(
                                fieldWithPath("[]").description("신고 사유 목록"),
                                fieldWithPath("[].reason").description("신고 사유 종류"),
                                fieldWithPath("[].message").description("신고 사유 메시지")
                        )
                ))
                .contentType(ContentType.JSON)
                .when().get("/user/report/reason")
                .then().log().all()
                .statusCode(200)
                .body("[0].reason", equalTo(Reason.INAPPROPRIATE_CONTENT.name()))
                .body("[0].message", equalTo(Reason.INAPPROPRIATE_CONTENT.getMessage()))
                .body("[1].reason", equalTo(Reason.SPAM_CONTENT.name()))
                .body("[1].message", equalTo(Reason.SPAM_CONTENT.getMessage()))
                .body("[2].reason", equalTo(Reason.ABUSIVE_LANGUAGE.name()))
                .body("[2].message", equalTo(Reason.ABUSIVE_LANGUAGE.getMessage()))
                .body("[3].reason", equalTo(Reason.COPYRIGHT_INFRINGEMENT.name()))
                .body("[3].message", equalTo(Reason.COPYRIGHT_INFRINGEMENT.getMessage()))
                .body("[4].reason", equalTo(Reason.OTHERS.name()))
                .body("[4].message", equalTo(Reason.OTHERS.getMessage()));
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

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("사용자를 삭제한다.")
    void deleteUser() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "사용자를 삭제합니다.",
                        "사용자 삭제 API"
                ))
                .contentType(ContentType.JSON)
                .when().delete("/user/me")
                .then().log().all()
                .statusCode(204);

        boolean exists = userRepository.existsByEmail("loki@pengcook.net");

        assertThat(exists).isFalse();
    }
}
