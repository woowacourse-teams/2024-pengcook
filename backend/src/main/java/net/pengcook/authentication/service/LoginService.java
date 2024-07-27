package net.pengcook.authentication.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import net.pengcook.authentication.dto.GoogleLoginRequest;
import net.pengcook.authentication.dto.GoogleLoginResponse;
import net.pengcook.authentication.dto.GoogleSignUpRequest;
import net.pengcook.authentication.dto.GoogleSignUpResponse;
import net.pengcook.authentication.dto.TokenPayload;
import net.pengcook.authentication.exception.DuplicationException;
import net.pengcook.authentication.exception.FirebaseTokenException;
import net.pengcook.authentication.util.JwtTokenManager;
import net.pengcook.authentication.util.TokenType;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {

    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final JwtTokenManager jwtTokenManager;

    public GoogleLoginResponse loginWithGoogle(GoogleLoginRequest googleLoginRequest) {
        FirebaseToken decodedToken = decodeIdToken(googleLoginRequest.idToken());
        String email = decodedToken.getEmail();

        if (!userRepository.existsByEmail(email)) {
            return new GoogleLoginResponse(false, null, null);
        }
        User user = userRepository.findByEmail(email);
        String accessToken = jwtTokenManager.createToken(new TokenPayload(user.getId(), user.getEmail(), TokenType.ACCESS));
        String refreshToken = jwtTokenManager.createToken(new TokenPayload(user.getId(), user.getEmail(), TokenType.REFRESH));

        return new GoogleLoginResponse(true, accessToken, refreshToken);
    }

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

    private User createUser(GoogleSignUpRequest googleSignUpRequest) {
        FirebaseToken decodedToken = decodeIdToken(googleSignUpRequest.idToken());

        return new User(
                decodedToken.getEmail(),
                googleSignUpRequest.username(),
                googleSignUpRequest.nickname(),
                decodedToken.getPicture(),
                googleSignUpRequest.birthday(),
                googleSignUpRequest.country()
        );
    }

    private FirebaseToken decodeIdToken(String idToken) {
        try {
            return firebaseAuth.verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new FirebaseTokenException("구글 인증에 실패했습니다.");
        }
    }
}
