package net.pengcook.authentication.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.JwtTokenManager;
import net.pengcook.authentication.domain.TokenExtractor;
import net.pengcook.authentication.domain.TokenPayload;
import net.pengcook.authentication.domain.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserInfoInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenManager jwtTokenManager;
    private final TokenExtractor tokenExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
            String accessToken = tokenExtractor.extractToken(authorizationHeader);

            TokenPayload tokenPayload = jwtTokenManager.extract(accessToken);
            tokenPayload.validateAccessToken("헤더에 토큰이 access token이 아닙니다.");
            UserInfo userInfo = new UserInfo(tokenPayload.userId(), tokenPayload.email());

            setUserInfoAttribute(userInfo);
        } catch (Exception e) {
            setUserInfoAttribute(null);
        }
        return true;
    }

    private void setUserInfoAttribute(UserInfo userInfo) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        requestAttributes.setAttribute(UserInfo.class.getName(), userInfo, RequestAttributes.SCOPE_REQUEST);
    }
}
