package net.pengcook.user.repository;

import java.util.List;
import net.pengcook.user.domain.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    List<UserBlock> findAllByBlockerId(long blockerId);

    void deleteByBlockeeId(long blockeeId);

    void deleteByBlockerId(long blockerId);
}
