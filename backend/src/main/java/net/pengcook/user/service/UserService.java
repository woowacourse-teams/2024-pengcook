package net.pengcook.user.service;

import lombok.AllArgsConstructor;
import net.pengcook.user.domain.User;
import net.pengcook.user.domain.UserReport;
import net.pengcook.user.dto.UserReportRequest;
import net.pengcook.user.dto.UserReportResponse;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.exception.NotFoundException;
import net.pengcook.user.repository.UserReportRepository;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserReportRepository userReportRepository;

    public UserResponse getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow();
        return new UserResponse(user);
    }

    public UsernameCheckResponse checkUsername(String username) {
        boolean userExists = userRepository.existsByUsername(username);
        return new UsernameCheckResponse(!userExists);
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
}
