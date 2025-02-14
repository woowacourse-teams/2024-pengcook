package net.pengcook.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.user.dto.FollowInfoResponse;
import net.pengcook.user.dto.ProfileResponse;
import net.pengcook.user.dto.ReportReasonResponse;
import net.pengcook.user.dto.ReportRequest;
import net.pengcook.user.dto.ReportResponse;
import net.pengcook.user.dto.UpdateProfileRequest;
import net.pengcook.user.dto.UpdateProfileResponse;
import net.pengcook.user.dto.UserBlockRequest;
import net.pengcook.user.dto.UserBlockResponse;
import net.pengcook.user.dto.UserFollowRequest;
import net.pengcook.user.dto.UserFollowResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.service.UserFollowService;
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
    private final UserFollowService userFollowService;

    @GetMapping("/user/me")
    public ProfileResponse getUserProfile(@LoginUser UserInfo userInfo) {
        return userService.getProfile(userInfo.getId(), userInfo.getId());
    }

    @GetMapping("/user/{userId}")
    public ProfileResponse getUserProfile(@LoginUser UserInfo userInfo, @PathVariable long userId) {
        return userService.getProfile(userInfo.getId(), userId);
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
    public ReportResponse report(
            @LoginUser UserInfo userInfo,
            @RequestBody @Valid ReportRequest reportRequest
    ) {
        return userService.report(userInfo.getId(), reportRequest);
    }

    @GetMapping("/user/report/reason")
    public List<ReportReasonResponse> getReportReasons() {
        return ReportReasonResponse.REASONS;
    }

    @PostMapping("/user/block")
    @ResponseStatus(HttpStatus.CREATED)
    public UserBlockResponse blockUser(
            @LoginUser UserInfo userInfo,
            @RequestBody @Valid UserBlockRequest userBlockRequest
    ) {
        return userService.blockUser(userInfo.getId(), userBlockRequest.blockeeId());
    }

    @PostMapping("/user/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public UserFollowResponse followUser(
            @LoginUser UserInfo userInfo,
            @RequestBody @Valid UserFollowRequest userFollowRequest
    ) {
        return userFollowService.followUser(userInfo.getId(), userFollowRequest.targetId());
    }

    @DeleteMapping("/user/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowUser(
            @LoginUser UserInfo userInfo,
            @RequestBody @Valid UserFollowRequest userFollowRequest
    ) {
        userFollowService.unfollowUser(userInfo.getId(), userFollowRequest.targetId());
    }

    @DeleteMapping("/user/follower")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFollower(
            @LoginUser UserInfo userInfo,
            @RequestBody @Valid UserFollowRequest userFollowRequest
    ) {
        userFollowService.unfollowUser(userFollowRequest.targetId(), userInfo.getId());
    }

    @GetMapping("/user/{userId}/follower")
    public FollowInfoResponse getFollowerInfo(@PathVariable long userId) {
        return userFollowService.getFollowerInfo(userId);
    }

    @GetMapping("/user/{userId}/following")
    public FollowInfoResponse getFollowingInfo(@PathVariable long userId) {
        return userFollowService.getFollowingInfo(userId);
    }
}
