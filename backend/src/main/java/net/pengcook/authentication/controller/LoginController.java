package net.pengcook.authentication.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.pengcook.authentication.dto.GoogleLoginRequest;
import net.pengcook.authentication.dto.GoogleLoginResponse;
import net.pengcook.authentication.dto.GoogleSignUpRequest;
import net.pengcook.authentication.dto.GoogleSignUpResponse;
import net.pengcook.authentication.dto.TokenRefreshRequest;
import net.pengcook.authentication.dto.TokenRefreshResponse;
import net.pengcook.authentication.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LoginController {

    private final LoginService LoginService;

    @PostMapping("/api/oauth/google/login")
    public GoogleLoginResponse loginWithGoogle(@RequestBody @Valid GoogleLoginRequest googleLoginRequest) {
        return LoginService.loginWithGoogle(googleLoginRequest);
    }

    @PostMapping("/api/oauth/google/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public GoogleSignUpResponse sighUpWithGoogle(@RequestBody @Valid GoogleSignUpRequest googleSignUpRequest) {
        return LoginService.signUpWithGoogle(googleSignUpRequest);
    }

    @PostMapping("/api/token/refresh")
    public TokenRefreshResponse refresh(@RequestBody @Valid TokenRefreshRequest tokenRefreshRequest) {
        return LoginService.refresh(tokenRefreshRequest.refreshToken());
    }
}
