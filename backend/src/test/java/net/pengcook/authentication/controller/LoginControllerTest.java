package net.pengcook.authentication.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.domain.JwtTokenManager;
import net.pengcook.authentication.domain.TokenPayload;
import net.pengcook.authentication.domain.TokenType;
import net.pengcook.authentication.dto.GoogleLoginRequest;
import net.pengcook.authentication.dto.GoogleLoginResponse;
import net.pengcook.authentication.dto.GoogleSignUpRequest;
import net.pengcook.authentication.dto.GoogleSignUpResponse;
import net.pengcook.authentication.dto.TokenRefreshRequest;
import net.pengcook.authentication.dto.TokenRefreshResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;

@Sql("/data/users.sql")
class LoginControllerTest extends RestDocsSetting {

    @MockBean
    private FirebaseAuth firebaseAuth;
    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Test
    @DisplayName("이미 가입된 계정으로 로그인하면 이미 가입되었다고 알리고 access token과 refresh token을 반환한다.")
    void loginWithGoogleWithEmailAlreadyRegistered() throws FirebaseAuthException {
        String email = "loki@pengcook.net";
        String idToken = "test.id.token";

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleLoginResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        requestFields(
                                fieldWithPath("idToken").description("Google ID Token")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("JWT Access Token"),
                                fieldWithPath("refreshToken").description("JWT Refresh Token"),
                                fieldWithPath("registered").description("사용자 등록 여부")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(new GoogleLoginRequest(idToken))
                .when().post("/api/oauth/google/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(GoogleLoginResponse.class);

        assertAll(
                () -> assertThat(actual.accessToken()).isNotNull(),
                () -> assertThat(actual.refreshToken()).isNotNull(),
                () -> assertThat(actual.registered()).isTrue()
        );
    }

    @Test
    @DisplayName("처음 로그인하면 가입되어 있지 않음을 알리고 access token과 refresh token을 반환하지 않는다.")
    void loginWithGoogleWithEmailNotRegistered() throws FirebaseAuthException {
        String email = "new_face@pengcook.net";
        String idToken = "test.id.token";

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleLoginResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        requestFields(
                                fieldWithPath("idToken").description("Google ID Token")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("JWT Access Token").optional(),
                                fieldWithPath("refreshToken").description("JWT Refresh Token").optional(),
                                fieldWithPath("registered").description("사용자 등록 여부")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(new GoogleLoginRequest(idToken))
                .when().post("/api/oauth/google/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(GoogleLoginResponse.class);

        assertAll(
                () -> assertThat(actual.accessToken()).isNull(),
                () -> assertThat(actual.refreshToken()).isNull(),
                () -> assertThat(actual.registered()).isFalse()
        );
    }

    @Test
    @DisplayName("구글 계정으로 회원가입을 할 수 있다.")
    void signUpWithGoogle() throws FirebaseAuthException {
        String email = "new_face@pengcook.net";
        String idToken = "test.id.token";
        GoogleSignUpRequest request = new GoogleSignUpRequest(
                idToken,
                "new_face",
                "신입",
                LocalDate.of(2000, 1, 1),
                "KOREA"
        );

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseToken.getPicture()).thenReturn("new_face.jpg");
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleSignUpResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        requestFields(
                                fieldWithPath("idToken").description("Google ID Token"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("birthday").description("생년월일"),
                                fieldWithPath("country").description("국가")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("image").description("사용자 프로필 이미지"),
                                fieldWithPath("birthday").description("사용자 생년월일"),
                                fieldWithPath("country").description("사용자 국가"),
                                fieldWithPath("accessToken").description("JWT Access Token"),
                                fieldWithPath("refreshToken").description("JWT Refresh Token")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/oauth/google/sign-up")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(GoogleSignUpResponse.class);

        assertThat(actual.email()).isEqualTo(email);
    }

    @Test
    @DisplayName("이미 가입된 계정으로 회원가입 하면 예외가 발생한다.")
    void signUpWithGoogleWhenEmailAleadyRegistered() throws FirebaseAuthException {
        String email = "loki@pengcook.net";
        String idToken = "test.id.token";
        GoogleSignUpRequest request = new GoogleSignUpRequest(
                idToken,
                "loki",
                "로키",
                LocalDate.of(1999, 8, 8),
                "KOREA"
        );

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseToken.getPicture()).thenReturn("loki.jpg");
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        requestFields(
                                fieldWithPath("idToken").description("Google ID Token"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("birthday").description("생년월일"),
                                fieldWithPath("country").description("국가")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/oauth/google/sign-up")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("refresh token으로 access token을 재발급한다.")
    void refresh() {
        String refreshToken = jwtTokenManager.createToken(new TokenPayload(1L, "tester@pengcook.net", TokenType.REFRESH));

        TokenRefreshResponse response = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        requestFields(
                                fieldWithPath("refreshToken").description("JWT Refresh Token")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("새로 발급된 JWT Access Token"),
                                fieldWithPath("refreshToken").description("새로 발급된 JWT Refresh Token")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(new TokenRefreshRequest(refreshToken))
                .when().post("/api/token/refresh")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(TokenRefreshResponse.class);

        assertAll(
                () -> assertThat(response.accessToken()).isNotBlank(),
                () -> assertThat(response.refreshToken()).isNotSameAs(refreshToken)
        );
    }
}
