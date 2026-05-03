package backend.academy.linktracker.scrapper.adapter.out.db.orm.repository;

import backend.academy.linktracker.scrapper.adapter.out.db.orm.entity.TagEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByName(String name);
}
