package net.pengcook.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.user.dto.UserReportRequest;
import net.pengcook.user.dto.UserReportResponse;
import net.pengcook.user.dto.UserBlockRequest;
import net.pengcook.user.dto.UserBlockResponse;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/user/me")
    public UserResponse getUserProfile(@LoginUser UserInfo userInfo) {
        return userService.getUserById(userInfo.getId());
    }

    @GetMapping("/api/user/username/check")
    public UsernameCheckResponse checkUsername(@RequestParam @NotBlank String username) {
        return userService.checkUsername(username);
    }

    @PostMapping("/api/user/report")
    @ResponseStatus(HttpStatus.CREATED)
    public UserReportResponse report(@LoginUser UserInfo userInfo, @RequestBody UserReportRequest userReportRequest) {
        return userService.reportUser(userInfo.getId(), userReportRequest);
    }

    @PostMapping("/api/user/block")
    @ResponseStatus(HttpStatus.CREATED)
    public UserBlockResponse blockUser(@LoginUser UserInfo userInfo, @RequestBody @Valid UserBlockRequest userBlockRequest) {
        return userService.blockUser(userInfo.getId(), userBlockRequest.blockeeId());
    }
}
