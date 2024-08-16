package net.pengcook.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.user.dto.ProfileResponse;
import net.pengcook.user.dto.UpdateProfileRequest;
import net.pengcook.user.dto.UpdateProfileResponse;
import net.pengcook.user.dto.UserBlockRequest;
import net.pengcook.user.dto.UserBlockResponse;
import net.pengcook.user.dto.UserReportRequest;
import net.pengcook.user.dto.UserReportResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/me")
    public ProfileResponse getUserProfile(@LoginUser UserInfo userInfo) {
        return userService.getUserById(userInfo.getId());
    }

    @GetMapping("/user/{userId}")
    public ProfileResponse getUserProfile(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/user/me")
    public UpdateProfileResponse updateUserProfile(
            @LoginUser UserInfo userInfo,
            @RequestBody @Valid UpdateProfileRequest updateProfileRequest
    ) {
        return userService.updateProfile(userInfo.getId(), updateProfileRequest);
    }

    @DeleteMapping("/user/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@LoginUser UserInfo userInfo) {
        userService.deleteUser(userInfo);
    }

    @GetMapping("/user/username/check")
    public UsernameCheckResponse checkUsername(@RequestParam @NotBlank String username) {
        return userService.checkUsername(username);
    }

    @PostMapping("/user/report")
    @ResponseStatus(HttpStatus.CREATED)
    public UserReportResponse report(
            @LoginUser UserInfo userInfo,
            @RequestBody UserReportRequest userReportRequest
    ) {
        return userService.reportUser(userInfo.getId(), userReportRequest);
    }

    @PostMapping("/user/block")
    @ResponseStatus(HttpStatus.CREATED)
    public UserBlockResponse blockUser(
            @LoginUser UserInfo userInfo,
            @RequestBody @Valid UserBlockRequest userBlockRequest
    ) {
        return userService.blockUser(userInfo.getId(), userBlockRequest.blockeeId());
    }
}
