package net.pengcook.follow.repository;

import net.pengcook.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    void deleteByFollowerIdAndFolloweeId(long followerId, long followeeId);
}
