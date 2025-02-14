package net.pengcook.user.repository;

import java.util.List;
import java.util.Optional;
import net.pengcook.user.domain.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    Optional<UserFollow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    List<UserFollow> findAllByFollowerId(long followerId);

    List<UserFollow> findAllByFolloweeId(long followeeId);

    boolean existsByFollowerIdAndFolloweeId(long followerId, long followeeId);
}
