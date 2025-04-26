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

public interface TestOwnableRepository extends OwnableRepository<TestOwnable, Long> {

    List<TestOwnable> findAllByOwnerIdInOrderByName(List<Long> ids);

    @Query("SELECT t FROM TestOwnable t WHERE t.ownerId = :ownerId")
    Set<TestOwnable> findByOwnerId(Long ownerId);

    @Query("SELECT t FROM TestOwnable t WHERE t.name = :name")
    Optional<TestOwnable> findByName(@Param("name") String name);

    @Query("SELECT t FROM TestOwnable t WHERE t.ownerId IN :ownerIds")
    List<TestOwnable> findByOwnerIdIn(@Param("ownerIds") List<Long> ownerIds);

    @Query("SELECT t FROM TestOwnable t WHERE t.name LIKE %:kw%")
    Page<TestOwnable> searchByNameLike(@Param("kw") String keyword, Pageable pageable);

    @Query("SELECT t FROM TestOwnable t WHERE t.name = :kw")
    Slice<TestOwnable> searchByName(@Param("kw") String keyword, Pageable pageable);
}
