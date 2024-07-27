package net.pengcook.authentication.domain;

import net.pengcook.authentication.exception.AuthorizationHeaderException;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    private static final String BEARER = "Bearer ";

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new AuthorizationHeaderException("인증 헤더가 존재하지 않습니다.");
        }
        if (!authorizationHeader.startsWith(BEARER)) {
            throw new AuthorizationHeaderException("인증 헤더는 Bearer로 시작해야 합니다.");
        }
        return authorizationHeader.substring(BEARER.length());
    }
}
