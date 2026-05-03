package backend.academy.linktracker.scrapper.adapter.out.db.orm.repository;

import backend.academy.linktracker.scrapper.adapter.out.db.orm.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatJpaRepository extends JpaRepository<ChatEntity, Long> {}
