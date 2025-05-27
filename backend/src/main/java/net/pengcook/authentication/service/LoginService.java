package net.pengcook.authentication.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pengcook.authentication.domain.JwtTokenManager;
import net.pengcook.authentication.domain.TokenPayload;
import net.pengcook.authentication.domain.TokenType;
import net.pengcook.authentication.dto.GoogleLoginRequest;
import net.pengcook.authentication.dto.GoogleLoginResponse;
import net.pengcook.authentication.dto.GoogleSignUpRequest;
import net.pengcook.authentication.dto.GoogleSignUpResponse;
import net.pengcook.authentication.dto.TokenRefreshResponse;
import net.pengcook.authentication.exception.DuplicationException;
import net.pengcook.authentication.exception.FirebaseTokenException;
import net.pengcook.authentication.exception.NoSuchUserException;
import net.pengcook.image.service.ImageClientService;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final JwtTokenManager jwtTokenManager;
    private final ImageClientService imageClientService;

    @Transactional(readOnly = true)
    public GoogleLoginResponse loginWithGoogle(GoogleLoginRequest googleLoginRequest) {
        FirebaseToken decodedToken = decodeIdToken(googleLoginRequest.idToken());
        String email = decodedToken.getEmail();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new GoogleLoginResponse(false, null, null);
        }
        String accessToken = jwtTokenManager.createToken(
                new TokenPayload(user.getId(), user.getEmail(), TokenType.ACCESS));
        String refreshToken = jwtTokenManager.createToken(
                new TokenPayload(user.getId(), user.getEmail(), TokenType.REFRESH));

        return new GoogleLoginResponse(true, accessToken, refreshToken);
    }

    @Transactional
    public GoogleSignUpResponse signUpWithGoogle(GoogleSignUpRequest googleSignUpRequest) {
        User user = createUser(googleSignUpRequest);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicationException("이미 가입된 이메일입니다.");
        }

        User savedUser = userRepository.save(user);
        String accessToken = jwtTokenManager.createToken(
                new TokenPayload(savedUser.getId(), savedUser.getEmail(), TokenType.ACCESS));
        String refreshToken = jwtTokenManager.createToken(
                new TokenPayload(savedUser.getId(), savedUser.getEmail(), TokenType.REFRESH));

        return new GoogleSignUpResponse(savedUser, accessToken, refreshToken);
    }

    public TokenRefreshResponse refresh(String refreshToken) {
        TokenPayload tokenPayload = jwtTokenManager.extract(refreshToken);
        tokenPayload.validateRefreshToken("refresh token이 아닙니다.");

        return new TokenRefreshResponse(
                jwtTokenManager.createToken(
                        new TokenPayload(tokenPayload.userId(), tokenPayload.email(), TokenType.ACCESS)),
                jwtTokenManager.createToken(tokenPayload)
        );
    }

    @Transactional(readOnly = true)
    public void checkToken(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("존재하지 않는 사용자입니다."));
    }

    private User createUser(GoogleSignUpRequest googleSignUpRequest) {
        FirebaseToken decodedToken = decodeIdToken(googleSignUpRequest.idToken());

        String userImage = googleSignUpRequest.image();
        if (userImage == null) {
            userImage = decodedToken.getPicture();
        }
        if (!userImage.startsWith("http")) {
            userImage = imageClientService.getImageUrl(userImage).url();
        }

        return new User(
                decodedToken.getEmail(),
                googleSignUpRequest.username(),
                googleSignUpRequest.nickname(),
                userImage,
                googleSignUpRequest.country(),
                googleSignUpRequest.introduction()
        );
    }

    private FirebaseToken decodeIdToken(String idToken) {
        try {
            return firebaseAuth.verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            log.error(e.getMessage(), e);
            throw new FirebaseTokenException("구글 인증에 실패했습니다.");
        }
    }
}
