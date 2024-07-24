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
import net.pengcook.authentication.exception.AuthenticationException;
import net.pengcook.authentication.util.JwtTokenManager;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
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
            return new GoogleLoginResponse(false, null);
        }
        User user = userRepository.findByEmail(email);
        String accessToken = jwtTokenManager.createToken(new TokenPayload(user.getId(), user.getEmail()));

        return new GoogleLoginResponse(true, accessToken);
    }

    public GoogleSignUpResponse signUpWithGoogle(GoogleSignUpRequest googleSignUpRequest) {
        User user = createUser(googleSignUpRequest);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다.");
        }

        User savedUser = userRepository.save(user);
        String accessToken = jwtTokenManager.createToken(new TokenPayload(savedUser.getId(), savedUser.getEmail()));

        return new GoogleSignUpResponse(savedUser, accessToken);
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
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "구글 인증에 실패했습니다.");
        }
    }
}
