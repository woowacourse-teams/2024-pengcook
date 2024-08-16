package net.pengcook.user.repository;

import net.pengcook.user.domain.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    void deleteByReporteeId(long reporteeId);

    void deleteByReporterId(long reporterId);
}
