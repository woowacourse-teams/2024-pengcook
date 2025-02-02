package net.pengcook.user.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.repository.CommentRepository;
import net.pengcook.image.service.ImageClientService;
import net.pengcook.like.repository.RecipeLikeRepository;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.recipe.service.RecipeService;
import net.pengcook.user.domain.BlockedUserGroup;
import net.pengcook.user.domain.User;
import net.pengcook.user.domain.UserBlock;
import net.pengcook.user.domain.UserFollow;
import net.pengcook.user.domain.UserReport;
import net.pengcook.user.dto.FollowInfoResponse;
import net.pengcook.user.dto.FollowUserInfoResponse;
import net.pengcook.user.dto.ProfileResponse;
import net.pengcook.user.dto.ReportRequest;
import net.pengcook.user.dto.ReportResponse;
import net.pengcook.user.dto.UpdateProfileRequest;
import net.pengcook.user.dto.UpdateProfileResponse;
import net.pengcook.user.dto.UserBlockResponse;
import net.pengcook.user.dto.UserFollowResponse;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.exception.BadArgumentException;
import net.pengcook.user.exception.NotFoundException;
import net.pengcook.user.exception.UserNotFoundException;
import net.pengcook.user.repository.UserBlockRepository;
import net.pengcook.user.repository.UserFollowRepository;
import net.pengcook.user.repository.UserReportRepository;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final RecipeService recipeService;

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final UserBlockRepository userBlockRepository;
    private final UserReportRepository userReportRepository;
    private final ImageClientService imageClientService;
    private final UserFollowRepository userFollowRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(long followerId, long followeeId) {
        User user = userRepository.findById(followeeId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        long recipeCount = recipeRepository.countByAuthorId(followeeId);
        boolean isFollow = userFollowRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        return new ProfileResponse(user, recipeCount, isFollow);
    }

    @Transactional(readOnly = true)
    public UsernameCheckResponse checkUsername(String username) {
        boolean userExists = userRepository.existsByUsername(username);
        return new UsernameCheckResponse(!userExists);
    }

    @Transactional
    public UpdateProfileResponse updateProfile(long userId, UpdateProfileRequest updateProfileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String userImage = updateProfileRequest.image();
        if (!userImage.startsWith("http")) {
            userImage = imageClientService.getImageUrl(userImage).url();
        }

        user.update(
                updateProfileRequest.username(),
                updateProfileRequest.nickname(),
                userImage,
                updateProfileRequest.region()
        );
        return new UpdateProfileResponse(user);
    }

    @Transactional
    public UserBlockResponse blockUser(long blockerId, long blockeeId) {
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new UserNotFoundException("정상적으로 로그인되지 않았습니다."));
        User blockee = userRepository.findById(blockeeId)
                .orElseThrow(() -> new UserNotFoundException("차단할 사용자를 찾을 수 없습니다."));

        UserBlock userBlock = userBlockRepository.save(new UserBlock(blocker, blockee));

        return new UserBlockResponse(new UserResponse(userBlock.getBlocker()),
                new UserResponse(userBlock.getBlockee()));
    }

    @Transactional
    public ReportResponse report(long reporterId, ReportRequest reportRequest) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new NotFoundException("신고자 정보를 조회할 수 없습니다."));
        User reportee = userRepository.findById(reportRequest.reporteeId())
                .orElseThrow(() -> new NotFoundException("피신고자 정보를 조회할 수 없습니다."));
        UserReport userReport = new UserReport(
                reporter,
                reportee,
                reportRequest.reason(),
                reportRequest.type(),
                reportRequest.targetId(),
                reportRequest.details()
        );

        UserReport savedUserReport = userReportRepository.save(userReport);
        return new ReportResponse(savedUserReport);
    }

    @Transactional(readOnly = true)
    public BlockedUserGroup getBlockedUserGroup(long blockerId) {

        return userBlockRepository.findAllByBlockerId(blockerId).stream()
                .map(UserBlock::getBlockee)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), BlockedUserGroup::new));
    }

    @Transactional
    public void deleteUser(UserInfo userInfo) {
        User user = userRepository.findById(userInfo.getId())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        commentRepository.deleteByUserId(userInfo.getId());
        recipeLikeRepository.deleteByUserId(userInfo.getId());
        userBlockRepository.deleteByBlockerId(userInfo.getId());
        userBlockRepository.deleteByBlockeeId(userInfo.getId());
        userReportRepository.deleteByReporterId(userInfo.getId());
        userReportRepository.deleteByReporteeId(userInfo.getId());

        List<Long> userRecipes = recipeRepository.findRecipeIdsByUserId(userInfo.getId());
        for (Long recipeId : userRecipes) {
            recipeService.deleteRecipe(userInfo, recipeId);
        }
        List<UserFollow> followings = userFollowRepository.findAllByFollowerId(userInfo.getId());
        for (UserFollow userFollow : followings) {
            userFollow.getFollowee().decreaseFollowerCount();
            userFollowRepository.delete(userFollow);
        }
        List<UserFollow> followers = userFollowRepository.findAllByFolloweeId(userInfo.getId());
        for (UserFollow userFollow : followers) {
            userFollow.getFollower().decreaseFolloweeCount();
            userFollowRepository.delete(userFollow);
        }
        userRepository.delete(user);
    }

    @Transactional
    public UserFollowResponse followUser(long followerId, long followeeId) {
        User follower = getUser(followerId);
        User followee = getUser(followeeId);
        boolean isFollow = userFollowRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isFollow) {
            throw new BadArgumentException("이미 팔로우 중입니다.");
        }

        UserFollow userFollow = new UserFollow(follower, followee);
        userFollowRepository.save(userFollow);
        follower.increaseFolloweeCount();
        followee.increaseFollowerCount();
        return new UserFollowResponse(userFollow);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        User follower = getUser(followerId);
        User followee = getUser(followeeId);
        UserFollow userFollow = userFollowRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .orElseThrow(() -> new NotFoundException("팔로우 관계를 찾을 수 없습니다."));

        userFollowRepository.delete(userFollow);
        follower.decreaseFolloweeCount();
        followee.decreaseFollowerCount();
    }

    @Transactional(readOnly = true)
    public FollowInfoResponse getFollowerInfo(long userId) {
        List<FollowUserInfoResponse> followers = userFollowRepository.findAllByFolloweeId(userId).stream()
                .map(userFollow -> new FollowUserInfoResponse(userFollow.getFollower()))
                .toList();

        return new FollowInfoResponse(followers);
    }

    @Transactional(readOnly = true)
    public FollowInfoResponse getFollowingInfo(long userId) {
        List<FollowUserInfoResponse> followings = userFollowRepository.findAllByFollowerId(userId).stream()
                .map(userFollow -> new FollowUserInfoResponse(userFollow.getFollowee()))
                .toList();

        return new FollowInfoResponse(followings);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자 정보를 조회할 수 없습니다."));
    }
}
