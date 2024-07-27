package net.pengcook.authentication.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import net.pengcook.authentication.dto.TokenPayload;
import net.pengcook.authentication.exception.JwtTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenManager {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";

    private final Algorithm secretAlgorithm;
    private final long tokenExpirationMills;

    public JwtTokenManager(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expire-in-millis}") long tokenExpirationMills
    ) {
        this.secretAlgorithm = Algorithm.HMAC512(secret);
        this.tokenExpirationMills = tokenExpirationMills;
    }

    public String createToken(TokenPayload payload) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiresAt = new Date(System.currentTimeMillis() + tokenExpirationMills);

        return JWT.create()
                .withSubject(String.valueOf(payload.userId()))
                .withClaim(CLAIM_EMAIL, payload.email())
                .withClaim(CLAIM_TOKEN_TYPE, payload.tokenType().name())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(secretAlgorithm);
    }

    public TokenPayload extract(String token) {
        JWTVerifier jwtVerifier = JWT.require(secretAlgorithm).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            return getTokenPayload(decodedJWT);
        } catch (JWTVerificationException e) {
            throw new JwtTokenException("유효하지 않은 토큰입니다.");
        }
    }

    private TokenPayload getTokenPayload(DecodedJWT decodedJWT) {
        long userId = Long.parseLong(decodedJWT.getSubject());
        String email = decodedJWT.getClaim(CLAIM_EMAIL).asString();
        TokenType tokenType = TokenType.valueOf(decodedJWT.getClaim(CLAIM_TOKEN_TYPE).asString());

        return new TokenPayload(userId, email, tokenType);
    }
}
