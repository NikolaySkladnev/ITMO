package backend.academy.linktracker.scrapper.adapter.out.db.orm.repository;

import backend.academy.linktracker.scrapper.adapter.out.db.orm.entity.LinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LinkJpaRepository extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByUrl(String url);

    @Query("""
        select l.url
        from LinkEntity l
        where exists (
            select 1
            from SubscriptionEntity s
            where s.link = l
        )
        order by l.id
        """)
    List<String> findTrackedUrls(Pageable pageable);

    @Modifying
    @Query("""
        delete from LinkEntity l
        where not exists (
            select 1
            from SubscriptionEntity s
            where s.link = l
        )
        """)
    int deleteOrphanLinks();
}
