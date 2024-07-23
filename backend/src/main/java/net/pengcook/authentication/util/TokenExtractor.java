package net.pengcook.authentication.util;

import net.pengcook.authentication.exception.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    private static final String BEARER = "Bearer ";

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "인증 헤더 없음", "인증 헤더가 존재하지 않습니다.");
        }
        if (!authorizationHeader.startsWith(BEARER)) {
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "인증 헤더 오류", "인증 헤더는 Bearer로 시작해야 합니다.");
        }
        return authorizationHeader.substring(BEARER.length());
    }
}
