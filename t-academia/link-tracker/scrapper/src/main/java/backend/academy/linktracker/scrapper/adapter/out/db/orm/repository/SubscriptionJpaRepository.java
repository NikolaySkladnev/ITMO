package backend.academy.linktracker.scrapper.adapter.out.db.orm.repository;

import backend.academy.linktracker.scrapper.adapter.out.db.orm.entity.SubscriptionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, Long> {

    boolean existsByChatChatIdAndLinkId(long chatId, long linkId);

    boolean existsByLinkId(long linkId);

    @EntityGraph(attributePaths = {"link", "tags"})
    Optional<SubscriptionEntity> findByChatChatIdAndLinkUrl(long chatId, String url);

    @EntityGraph(attributePaths = {"link", "tags"})
    List<SubscriptionEntity> findDistinctByChatChatIdOrderByIdAsc(long chatId, Pageable pageable);

    @EntityGraph(attributePaths = {"chat"})
    List<SubscriptionEntity> findDistinctByLinkUrlOrderByIdAsc(String url, Pageable pageable);
}
