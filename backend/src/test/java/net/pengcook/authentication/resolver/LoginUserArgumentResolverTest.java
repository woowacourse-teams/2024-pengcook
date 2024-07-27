package net.pengcook.authentication.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.dto.TokenPayload;
import net.pengcook.authentication.exception.JwtTokenException;
import net.pengcook.authentication.util.JwtTokenManager;
import net.pengcook.authentication.util.TokenExtractor;
import net.pengcook.authentication.util.TokenType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.request.NativeWebRequest;

@SpringBootTest
class LoginUserArgumentResolverTest {

    @Autowired
    private JwtTokenManager jwtTokenManager;
    @Autowired
    private TokenExtractor tokenExtractor;

    @Test
    @DisplayName("로그인한 사용자 정보를 추출한다.")
    void resolveArgument() {
        LoginUserArgumentResolver loginUserArgumentResolver = new LoginUserArgumentResolver(jwtTokenManager, tokenExtractor);
        String accessToken = jwtTokenManager.createToken(new TokenPayload(1L, "tester@pengcook.net", TokenType.ACCESS));
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getHeader("Authorization")).thenReturn("Bearer " + accessToken);

        UserInfo actual = loginUserArgumentResolver.resolveArgument(null, null, webRequest, null);

        assertThat(actual.getEmail()).isEqualTo("tester@pengcook.net");
    }

    @Test
    @DisplayName("Authorization 헤더에 refresh 토큰을 넣으면 예외가 발생한다.")
    void resolveArgumentWhenAuthorizationHeaderRefreshToken() {
        LoginUserArgumentResolver loginUserArgumentResolver = new LoginUserArgumentResolver(jwtTokenManager, tokenExtractor);
        String refresh = jwtTokenManager.createToken(new TokenPayload(1L, "tester@pengcook.net", TokenType.REFRESH));
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getHeader("Authorization")).thenReturn("Bearer " + refresh);

        assertThatThrownBy(() -> loginUserArgumentResolver.resolveArgument(null, null, webRequest, null))
                .isInstanceOf(JwtTokenException.class)
                .hasMessage("헤더에 토큰이 access token이 아닙니다.");
    }
}
