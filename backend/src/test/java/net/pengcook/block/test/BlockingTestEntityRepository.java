package net.pengcook.block.test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.pengcook.block.repository.OwnableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockingTestEntityRepository extends OwnableRepository<BlockingTestEntity, Long> {

    List<BlockingTestEntity> findAllByOwnerIdInOrderByName(List<Long> ids);

    @Query("SELECT t FROM BlockingTestEntity t WHERE t.ownerId = :ownerId")
    Set<BlockingTestEntity> findByOwnerId(Long ownerId);

    @Query("SELECT t FROM BlockingTestEntity t WHERE t.name = :name")
    Optional<BlockingTestEntity> findByName(@Param("name") String name);

    @Query("SELECT t FROM BlockingTestEntity t WHERE t.ownerId IN :ownerIds")
    List<BlockingTestEntity> findByOwnerIdIn(@Param("ownerIds") List<Long> ownerIds);

    @Query("SELECT t FROM BlockingTestEntity t WHERE t.name LIKE %:kw%")
    Page<BlockingTestEntity> searchByNameLike(@Param("kw") String keyword, Pageable pageable);

    @Query("SELECT t FROM BlockingTestEntity t WHERE t.name = :kw")
    Slice<BlockingTestEntity> searchByName(@Param("kw") String keyword, Pageable pageable);
}
