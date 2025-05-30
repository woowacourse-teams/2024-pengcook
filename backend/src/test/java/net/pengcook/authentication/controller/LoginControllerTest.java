package net.pengcook.authentication.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.regex.Pattern;
import net.pengcook.RestDocsSetting;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
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

@WithLoginUserTest
@Sql("/data/users.sql")
class LoginControllerTest extends RestDocsSetting {

    private static final Pattern JWT_PATTERN = Pattern.compile("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");

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
                        "이미 가입된 계정으로 로그인하면 토큰을 반환하고, 처음 로그인하면 등록되지 않은 계정을 알립니다.",
                        "구글 로그인 API",
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
                .when().post("/oauth/google/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(GoogleLoginResponse.class);

        assertAll(
                () -> assertThat(actual.accessToken()).matches(JWT_PATTERN),
                () -> assertThat(actual.refreshToken()).matches(JWT_PATTERN),
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
                        "이미 가입된 계정으로 로그인하면 토큰을 반환하고, 처음 로그인하면 등록되지 않은 계정을 알립니다.",
                        "구글 로그인 API",
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
                .when().post("/oauth/google/login")
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
    @DisplayName("구글 계정으로 회원가입을 한다.")
    void signUpWithGoogle() throws FirebaseAuthException {
        String email = "new_face@pengcook.net";
        String idToken = "test.id.token";
        GoogleSignUpRequest request = new GoogleSignUpRequest(
                idToken,
                "new_face",
                "신입",
                "KOREA",
                "new_face.jpg"
        );

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseToken.getPicture()).thenReturn("new_face.jpg");
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleSignUpResponse actual = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "구글 계정으로 새로운 사용자를 등록합니다.",
                        "구글 회원가입 API",
                        requestFields(
                                fieldWithPath("idToken").description("Google ID Token"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("country").description("국가"),
                                fieldWithPath("image").description("사용자 사진")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("image").description("사용자 프로필 이미지"),
                                fieldWithPath("country").description("사용자 국가"),
                                fieldWithPath("accessToken").description("JWT Access Token"),
                                fieldWithPath("refreshToken").description("JWT Refresh Token")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/oauth/google/sign-up")
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
                "KOREA",
                "loki.jpg"
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
                                fieldWithPath("country").description("국가"),
                                fieldWithPath("image").description("사용자 사진")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/oauth/google/sign-up")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("refresh token으로 access token을 재발급한다.")
    void refresh() {
        String refreshToken = jwtTokenManager.createToken(
                new TokenPayload(1L, "tester@pengcook.net", TokenType.REFRESH));

        TokenRefreshResponse response = RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "Refresh token을 사용하여 새로운 access token과 refresh token을 발급합니다.",
                        "토큰 재발급 API",
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
                .when().post("/token/refresh")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(TokenRefreshResponse.class);

        assertAll(
                () -> assertThat(response.accessToken()).matches(JWT_PATTERN),
                () -> assertThat(response.refreshToken()).isNotSameAs(refreshToken)
        );
    }

    @Test
    @WithLoginUser(email = "loki@pengcook.net")
    @DisplayName("로그인이 되었는지 확인한다.")
    void checkToken() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "로그인이 되었는지 확인합니다.",
                        "로그인 확인 API"
                ))
                .contentType(ContentType.JSON)
                .when().get("/token/check")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("로그인이 되어있지 않으면 예외가 발생한다.")
    void checkTokenWhenNoLogin() {
        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH))
                .contentType(ContentType.JSON)
                .when().get("/token/check")
                .then().log().all()
                .statusCode(404);
    }
}
