package net.pengcook.follow.service;

import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.follow.entity.Follow;
import net.pengcook.follow.exception.UserNotFoundException;
import net.pengcook.follow.repository.FollowRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void follow(UserInfo userInfo, long followeeId) {
        User follower = userRepository.findById(userInfo.getId())
                .orElseThrow(() -> new UserNotFoundException("팔로워가 존재하지 않습니다."));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new UserNotFoundException("팔로이가 존재하지 않습니다."));

        followRepository.save(new Follow(follower, followee));
    }

    public void unfollow(UserInfo userInfo, long followeeId) {
        followRepository.deleteByFollowerIdAndFolloweeId(userInfo.getId(), followeeId);
    }
}
