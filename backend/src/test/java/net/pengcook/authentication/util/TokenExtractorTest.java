package net.pengcook.authentication.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenExtractorTest {

    TokenExtractor tokenExtractor = new TokenExtractor();

    @Test
    @DisplayName("authorizationHeader에서 토큰을 추출한다.")
    void extractToken() {
        String authorizationHeader = "Bearer token";

        String token = tokenExtractor.extractToken(authorizationHeader);

        assertThat(token).isEqualTo("token");
    }

    @Test
    @DisplayName("authorizationHeader에서 토큰을 추출할 때 authorizationHeader가 null이면 예외를 던진다.")
    void extractTokenWhenAuthorizationHeaderNull() {
        String authorizationHeader = null;

        assertThatThrownBy(() -> tokenExtractor.extractToken(authorizationHeader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Authorization header is required.");
    }

    @Test
    @DisplayName("authorizationHeader에서 토큰을 추출할 때 authorizationHeader가 'Bearer '로 시작하지 않으면이면 예외를 던진다.")
    void extractTokenWhenAuthorizationHeaderNotStartWithBearer() {
        String authorizationHeader = "Not Bearer token";

        assertThatThrownBy(() -> tokenExtractor.extractToken(authorizationHeader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Authorization header.");
    }
}