package net.pengcook.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.user.domain.User;
import net.pengcook.user.domain.UserFollow;
import net.pengcook.user.dto.FollowInfoResponse;
import net.pengcook.user.dto.FollowUserInfoResponse;
import net.pengcook.user.dto.UserFollowResponse;
import net.pengcook.user.exception.IllegalStateException;
import net.pengcook.user.exception.NotFoundException;
import net.pengcook.user.repository.UserBlockRepository;
import net.pengcook.user.repository.UserFollowRepository;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;

    @Transactional
    public UserFollowResponse followUser(long followerId, long followeeId) {
        User follower = getUser(followerId);
        User followee = getUser(followeeId);
        validate(followerId, followeeId);

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

    @Transactional
    public void blockUserFollow(long blockerId, long blockeeId) {
        userFollowRepository.findByFollowerIdAndFolloweeId(blockerId, blockeeId)
                .ifPresent((userFollow) -> unfollowUser(blockerId, blockeeId));
        userFollowRepository.findByFollowerIdAndFolloweeId(blockeeId, blockerId)
                .ifPresent((userFollow) -> unfollowUser(blockeeId, blockerId));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자 정보를 조회할 수 없습니다."));
    }

    private void validate(long followerId, long followeeId) {
        if (userFollowRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalStateException("이미 팔로우 중입니다.");
        }
        if (userBlockRepository.existsByBlockerIdAndBlockeeId(followerId, followeeId)
                || userBlockRepository.existsByBlockerIdAndBlockeeId(followeeId, followerId)) {
            throw new IllegalStateException("팔로우 할 수 없습니다.");
        }
    }
}
