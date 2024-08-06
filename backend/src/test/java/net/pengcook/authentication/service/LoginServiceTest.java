package net.pengcook.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import java.util.regex.Pattern;
import net.pengcook.authentication.domain.JwtTokenManager;
import net.pengcook.authentication.domain.TokenPayload;
import net.pengcook.authentication.domain.TokenType;
import net.pengcook.authentication.dto.GoogleLoginRequest;
import net.pengcook.authentication.dto.GoogleLoginResponse;
import net.pengcook.authentication.dto.GoogleSignUpRequest;
import net.pengcook.authentication.dto.GoogleSignUpResponse;
import net.pengcook.authentication.dto.TokenRefreshResponse;
import net.pengcook.authentication.exception.DuplicationException;
import net.pengcook.authentication.exception.JwtTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;


@DataJpaTest
@Import({LoginService.class, JwtTokenManager.class})
@Sql(scripts = "/data/users.sql")
class LoginServiceTest {

    private static final Pattern JWT_PATTERN = Pattern.compile("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");

    @MockBean
    private FirebaseAuth firebaseAuth;
    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Test
    @DisplayName("이미 가입된 계정으로 로그인하면 이미 가입되었다고 알리고 access token을 반환한다.")
    void loginWithGoogleWithEmailAlreadyRegistered() throws FirebaseAuthException {
        String email = "loki@pengcook.net";
        String idToken = "test.id.token";
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleLoginResponse googleLoginResponse = loginService.loginWithGoogle(request);

        assertAll(
                () -> assertThat(googleLoginResponse.accessToken()).matches(JWT_PATTERN),
                () -> assertThat(googleLoginResponse.registered()).isTrue()
        );
    }

    @Test
    @DisplayName("처음 로그인하면 가입되어 있지 않음을 알리고 access token을 반환하지 않는다.")
    void loginWithGoogleWithEmailNotRegistered() throws FirebaseAuthException {
        String email = "new_face@pengcook.net";
        String idToken = "test.id.token";
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleLoginResponse googleLoginResponse = loginService.loginWithGoogle(request);

        assertAll(
                () -> assertThat(googleLoginResponse.accessToken()).isNull(),
                () -> assertThat(googleLoginResponse.registered()).isFalse()
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
                "KOREA"
        );

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseToken.getPicture()).thenReturn("new_face.jpg");
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        GoogleSignUpResponse googleSignUpResponse = loginService.signUpWithGoogle(request);
        String actual = googleSignUpResponse.email();

        assertThat(actual).isEqualTo(email);
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
                "KOREA"
        );

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getEmail()).thenReturn(email);
        when(firebaseToken.getPicture()).thenReturn("loki.jpg");
        when(firebaseAuth.verifyIdToken(idToken)).thenReturn(firebaseToken);

        assertThatThrownBy(() -> loginService.signUpWithGoogle(request))
                .isInstanceOf(DuplicationException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }

    @Test
    @DisplayName("refresh token을 이용해 access token을 재발급한다.")
    void refresh() {
        String refreshToken = jwtTokenManager.createToken(new TokenPayload(1L, "tester@pengcook.net", TokenType.REFRESH));

        TokenRefreshResponse refresh = loginService.refresh(refreshToken);

        assertAll(
                () -> assertThat(refresh.accessToken()).matches(JWT_PATTERN),
                () -> assertThat(refresh.refreshToken()).matches(JWT_PATTERN).isNotSameAs(refreshToken)
        );
    }

    @Test
    @DisplayName("refresh token이 유효하지 않으면 예외가 발생한다.")
    void refreshWhenWithAccessToken() {
        String accessToken = jwtTokenManager.createToken(new TokenPayload(1L, "tester@pengcook.net", TokenType.ACCESS));

        assertThatThrownBy(() -> loginService.refresh(accessToken))
                .isInstanceOf(JwtTokenException.class)
                .hasMessage("refresh token이 아닙니다.");
    }

    @Test
    @DisplayName("refresh token이 유효하지 않으면 예외가 발생한다.")
    void refreshWhenRefreshTokenInvalid() {
        String refreshToken = "invalid.refresh.token";

        assertThatThrownBy(() -> loginService.refresh(refreshToken))
                .isInstanceOf(JwtTokenException.class)
                .hasMessage("유효하지 않은 토큰입니다.");
    }
}
