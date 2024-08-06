package net.pengcook.authentication.resolver;

import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.JwtTokenManager;
import net.pengcook.authentication.domain.TokenExtractor;
import net.pengcook.authentication.domain.TokenPayload;
import net.pengcook.authentication.domain.UserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenManager jwtTokenManager;
    private final TokenExtractor tokenExtractor;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) != null;
    }

    @Override
    public UserInfo resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {

        String authorizationHeader = webRequest.getHeader(AUTHORIZATION_HEADER);
        String accessToken = tokenExtractor.extractToken(authorizationHeader);

        TokenPayload tokenPayload = jwtTokenManager.extract(accessToken);
        tokenPayload.validateAccessToken("헤더에 토큰이 access token이 아닙니다.");

        return new UserInfo(tokenPayload.userId(), tokenPayload.email());
    }
}
