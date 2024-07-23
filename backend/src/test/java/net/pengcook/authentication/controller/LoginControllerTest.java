package net.pengcook.authentication.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import net.pengcook.authentication.dto.GoogleLoginRequest;
import net.pengcook.authentication.dto.GoogleLoginResponse;
import net.pengcook.authentication.dto.GoogleSignUpRequest;
import net.pengcook.authentication.dto.GoogleSignUpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("/data/users.sql")
class LoginControllerTest {

    @LocalServerPort
    int port;
    @MockBean
    private FirebaseAuth firebaseAuth;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("이미 가입된 계정으로 로그인하면 이미 가입되었다고 알리고 access token을 반환한다.")
    void loginWithGoogleWithEmailAlreadyRegistered() throws FirebaseAuthException {
        String email = "loki@pengcook.net";
        String idToken = "test.id.token";

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleLoginResponse actual = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new GoogleLoginRequest(idToken))
                .when().post("/api/oauth/google/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(GoogleLoginResponse.class);

        assertAll(
                () -> assertThat(actual.accessToken()).isNotNull(),
                () -> assertThat(actual.registered()).isTrue()
        );
    }

    @Test
    @DisplayName("처음 로그인하면 가입되어 있지 않음을 알리고 access token을 반환하지 않는다.")
    void loginWithGoogleWithEmailNotRegistered() throws FirebaseAuthException {
        String email = "new_face@pengcook.net";
        String idToken = "test.id.token";

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleLoginResponse actual = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new GoogleLoginRequest(idToken))
                .when().post("/api/oauth/google/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(GoogleLoginResponse.class);

        assertAll(
                () -> assertThat(actual.accessToken()).isNull(),
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

        GoogleSignUpResponse actual = RestAssured.given().log().all()
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

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/oauth/google/sign-up")
                .then().log().all()
                .statusCode(400);
    }
}
