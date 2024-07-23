package net.pengcook.authentication.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import net.pengcook.authentication.dto.TokenPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenManagerTest {
    private static final String JWT_REGEX = "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$";

    private final JwtTokenManager jwtTokenManager = new JwtTokenManager("testSecretKey", 3600000L);

    @Test
    @DisplayName("access token을 생성한다.")
    void createToken() {
        TokenPayload payload = new TokenPayload(1L, "test@pengcook.net");

        String accessToken = jwtTokenManager.createToken(payload);

        assertThat(accessToken).matches(JWT_REGEX);
    }

    @Test
    @DisplayName("access token의 정보를 추출한다.")
    void extract() {
        TokenPayload expected = new TokenPayload(1L, "test@pengcook.net");
        TokenPayload payload = new TokenPayload(1L, "test@pengcook.net");
        String accessToken = jwtTokenManager.createToken(payload);

        TokenPayload actual = jwtTokenManager.extract(accessToken);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("유효하지 않은 access token을 추출하면 예외가 발생한다.")
    void extractWhenInvalidToken() {
        String accessToken = "fakefakefakefakefake.accessaccessaccessaccess.tokentokentokentokentoken";

        assertThatThrownBy(() -> jwtTokenManager.extract(accessToken))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
