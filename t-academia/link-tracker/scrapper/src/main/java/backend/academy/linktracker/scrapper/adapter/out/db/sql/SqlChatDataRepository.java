package backend.academy.linktracker.scrapper.adapter.out.db.sql;

import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "sql", matchIfMissing = true)
public class SqlChatDataRepository implements ChatDataRepository {

    private final JdbcClient jdbcClient;

    @Override
    public boolean addChat(Chat chat) {
        return jdbcClient.sql("""
                insert into chats (chat_id)
                values (:chatId)
                on conflict (chat_id) do nothing
                """).param("chatId", chat.chatId()).update() > 0;
    }

    @Override
    @Transactional
    public boolean removeChat(Chat chat) {
        int deleted = jdbcClient
                .sql("delete from chats where chat_id = :chatId")
                .param("chatId", chat.chatId())
                .update();

        if (deleted == 0) {
            return false;
        }

        deleteOrphanLinks();
        return true;
    }

    @Override
    public boolean hasChat(Chat chat) {
        Integer found = jdbcClient
                .sql("select 1 from chats where chat_id = :chatId")
                .param("chatId", chat.chatId())
                .query(Integer.class)
                .optional()
                .orElse(null);

        return found != null;
    }

    @Override
    @Transactional
    public boolean addLinkToChat(Chat chat, Link link) {
        jdbcClient.sql("""
                    insert into links (url)
                    values (:url)
                    on conflict (url) do nothing
                    """).param("url", link.url()).update();

        Long linkId = findLinkId(link.url());
        int inserted = jdbcClient
                .sql("""
                    insert into subscriptions (chat_id, link_id)
                    values (:chatId, :linkId)
                    on conflict (chat_id, link_id) do nothing
                    """)
                .param("chatId", chat.chatId())
                .param("linkId", linkId)
                .update();

        if (inserted == 0) {
            return false;
        }

        Long subscriptionId = findSubscriptionId(chat.chatId(), link.url());
        for (String tag : link.tags()) {
            jdbcClient.sql("""
                        insert into tags (name)
                        values (:name)
                        on conflict (name) do nothing
                        """).param("name", tag).update();

            Long tagId = findTagId(tag);
            jdbcClient
                    .sql("""
                        insert into subscription_tags (subscription_id, tag_id)
                        values (:subscriptionId, :tagId)
                        on conflict (subscription_id, tag_id) do nothing
                        """)
                    .param("subscriptionId", subscriptionId)
                    .param("tagId", tagId)
                    .update();
        }

        return true;
    }

    @Override
    @Transactional
    public Optional<Link> removeLinkFromChat(Chat chat, Link link) {
        Optional<Link> removed = findLinkByChat(chat, link.url());
        if (removed.isEmpty()) {
            return Optional.empty();
        }

        Long subscriptionId = findSubscriptionId(chat.chatId(), link.url());
        jdbcClient
                .sql("delete from subscriptions where id = :subscriptionId")
                .param("subscriptionId", subscriptionId)
                .update();

        deleteOrphanLinks();
        return removed;
    }

    @Override
    public Optional<Link> findLinkByChat(Chat chat, String url) {
        List<LinkRow> rows = jdbcClient
                .sql("""
                    select s.id as subscription_id, l.url, t.name as tag_name
                    from subscriptions s
                    join links l on l.id = s.link_id
                    left join subscription_tags st on st.subscription_id = s.id
                    left join tags t on t.id = st.tag_id
                    where s.chat_id = :chatId and l.url = :url
                    order by s.id, t.name
                    """)
                .param("chatId", chat.chatId())
                .param("url", url)
                .query((rs, _) ->
                        new LinkRow(rs.getLong("subscription_id"), rs.getString("url"), rs.getString("tag_name")))
                .list();

        return toLinks(rows).stream().findFirst();
    }

    @Override
    public List<Link> getAllLinksByChat(Chat chat, int offset, int limit) {
        List<LinkRow> rows = jdbcClient
                .sql("""
                    with page as (
                        select s.id as subscription_id, l.url
                        from subscriptions s
                        join links l on l.id = s.link_id
                        where s.chat_id = :chatId
                        order by s.id
                        limit :limit offset :offset
                    )
                    select page.subscription_id, page.url, t.name as tag_name
                    from page
                    left join subscription_tags st on st.subscription_id = page.subscription_id
                    left join tags t on t.id = st.tag_id
                    order by page.subscription_id, t.name
                    """)
                .param("chatId", chat.chatId())
                .param("limit", limit)
                .param("offset", offset)
                .query((rs, _) ->
                        new LinkRow(rs.getLong("subscription_id"), rs.getString("url"), rs.getString("tag_name")))
                .list();

        return toLinks(rows);
    }

    @Override
    public List<String> getTrackedLinksPage(int offset, int limit) {
        return jdbcClient
                .sql("""
                    select l.url
                    from links l
                    where exists (
                        select 1
                        from subscriptions s
                        where s.link_id = l.id
                    )
                    order by l.id
                    limit :limit offset :offset
                    """)
                .param("limit", limit)
                .param("offset", offset)
                .query(String.class)
                .list();
    }

    @Override
    public List<Chat> getAllChatByLink(Link link, int offset, int limit) {
        return jdbcClient
                .sql("""
                    select c.chat_id
                    from subscriptions s
                    join chats c on c.chat_id = s.chat_id
                    join links l on l.id = s.link_id
                    where l.url = :url
                    order by s.id
                    limit :limit offset :offset
                    """)
                .param("url", link.url())
                .param("limit", limit)
                .param("offset", offset)
                .query((rs, _) -> new Chat(rs.getLong("chat_id")))
                .list();
    }

    @Override
    public Instant getLastUpdateTime(Link link) {
        Timestamp timestamp = jdbcClient
                .sql("select last_updated_at from links where url = :url")
                .param("url", link.url())
                .query(Timestamp.class)
                .optional()
                .orElse(null);

        return timestamp == null ? null : timestamp.toInstant();
    }

    @Override
    public void setLastUpdateTime(Link link, Instant lastUpdateTime) {
        jdbcClient
                .sql("update links set last_updated_at = :lastUpdatedAt where url = :url")
                .param("lastUpdatedAt", Timestamp.from(lastUpdateTime))
                .param("url", link.url())
                .update();
    }

    private Long findLinkId(String url) {
        return jdbcClient
                .sql("select id from links where url = :url")
                .param("url", url)
                .query(Long.class)
                .single();
    }

    private Long findSubscriptionId(long chatId, String url) {
        return jdbcClient
                .sql("""
                    select s.id
                    from subscriptions s
                    join links l on l.id = s.link_id
                    where s.chat_id = :chatId and l.url = :url
                    """)
                .param("chatId", chatId)
                .param("url", url)
                .query(Long.class)
                .single();
    }

    private Long findTagId(String tag) {
        return jdbcClient
                .sql("select id from tags where name = :name")
                .param("name", tag)
                .query(Long.class)
                .single();
    }

    private void deleteOrphanLinks() {
        jdbcClient.sql("""
                    delete from links l
                    where not exists (
                        select 1
                        from subscriptions s
                        where s.link_id = l.id
                    )
                    """).update();
    }

    private List<Link> toLinks(List<LinkRow> rows) {
        Map<Long, LinkAccumulator> result = new LinkedHashMap<>();

        for (LinkRow row : rows) {
            LinkAccumulator accumulator = result.computeIfAbsent(
                    row.subscriptionId(), ignored -> new LinkAccumulator(row.url(), new ArrayList<>()));

            if (row.tagName() != null && !row.tagName().isBlank()) {
                accumulator.tags().add(row.tagName());
            }
        }

        return result.values().stream()
                .map(item -> new Link(item.url(), List.copyOf(item.tags())))
                .toList();
    }

    private record LinkRow(long subscriptionId, String url, String tagName) {}

    private record LinkAccumulator(String url, List<String> tags) {}
}
