package net.pengcook.user.service;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import net.pengcook.user.domain.BlockedUserGroup;
import net.pengcook.user.domain.User;
import net.pengcook.user.domain.UserReport;
import net.pengcook.user.dto.UserReportRequest;
import net.pengcook.user.dto.UserReportResponse;
import net.pengcook.user.domain.UserBlock;
import net.pengcook.user.dto.UserBlockResponse;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.exception.NotFoundException;
import net.pengcook.user.repository.UserReportRepository;
import net.pengcook.user.exception.UserNotFoundException;
import net.pengcook.user.repository.UserBlockRepository;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;
    private final UserReportRepository userReportRepository;

    public UserResponse getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return new UserResponse(user);
    }

    public UsernameCheckResponse checkUsername(String username) {
        boolean userExists = userRepository.existsByUsername(username);
        return new UsernameCheckResponse(!userExists);
    }

    public UserBlockResponse blockUser(long blockerId, long blockeeId) {
        User blocker = userRepository.findById(blockerId).orElseThrow(() -> new UserNotFoundException("정상적으로 로그인되지 않았습니다."));
        User blockee = userRepository.findById(blockeeId).orElseThrow(() -> new UserNotFoundException("차단할 사용자를 찾을 수 없습니다."));

        UserBlock userBlock = userBlockRepository.save(new UserBlock(blocker, blockee));

        return new UserBlockResponse(new UserResponse(userBlock.getBlocker()), new UserResponse(userBlock.getBlockee()));
    }

    public UserReportResponse reportUser(long reporterId, UserReportRequest userReportRequest) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new NotFoundException("신고자 정보를 조회할 수 없습니다."));
        User reportee = userRepository.findById(userReportRequest.reporteeId())
                .orElseThrow(() -> new NotFoundException("피신고자 정보를 조회할 수 없습니다."));
        UserReport userReport = new UserReport(
                reporter,
                reportee,
                userReportRequest.reason(),
                userReportRequest.details()
        );

        UserReport savedUserReport = userReportRepository.save(userReport);
        return new UserReportResponse(savedUserReport);
    }

    public BlockedUserGroup getBlockedUserGroup(long blockerId) {

        return userBlockRepository.findAllByBlockerId(blockerId).stream()
                .map(UserBlock::getBlockee)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), BlockedUserGroup::new));
    }
}
