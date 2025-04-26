package net.pengcook.block.repository;

import net.pengcook.user.domain.Ownable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OwnableRepository<T extends Ownable, ID> extends JpaRepository<T, ID> {
}
