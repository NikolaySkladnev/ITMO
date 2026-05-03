package backend.academy.linktracker.scrapper.adapter.out.db.orm;

import backend.academy.linktracker.scrapper.adapter.out.db.orm.entity.ChatEntity;
import backend.academy.linktracker.scrapper.adapter.out.db.orm.entity.LinkEntity;
import backend.academy.linktracker.scrapper.adapter.out.db.orm.entity.SubscriptionEntity;
import backend.academy.linktracker.scrapper.adapter.out.db.orm.entity.TagEntity;
import backend.academy.linktracker.scrapper.adapter.out.db.orm.repository.ChatJpaRepository;
import backend.academy.linktracker.scrapper.adapter.out.db.orm.repository.LinkJpaRepository;
import backend.academy.linktracker.scrapper.adapter.out.db.orm.repository.SubscriptionJpaRepository;
import backend.academy.linktracker.scrapper.adapter.out.db.orm.repository.TagJpaRepository;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "orm")
public class OrmChatDataRepository implements ChatDataRepository {

    private final ChatJpaRepository chatRepository;
    private final LinkJpaRepository linkRepository;
    private final SubscriptionJpaRepository subscriptionRepository;
    private final TagJpaRepository tagRepository;

    @Override
    public boolean addChat(Chat chat) {
        if (chatRepository.existsById(chat.chatId())) {
            return false;
        }

        chatRepository.save(new ChatEntity(chat.chatId()));
        return true;
    }

    @Override
    @Transactional
    public boolean removeChat(Chat chat) {
        if (!chatRepository.existsById(chat.chatId())) {
            return false;
        }

        chatRepository.deleteById(chat.chatId());
        chatRepository.flush();
        linkRepository.deleteOrphanLinks();
        return true;
    }

    @Override
    public boolean hasChat(Chat chat) {
        return chatRepository.existsById(chat.chatId());
    }

    @Override
    @Transactional
    public boolean addLinkToChat(Chat chat, Link link) {
        LinkEntity linkEntity =
                linkRepository.findByUrl(link.url()).orElseGet(() -> linkRepository.save(new LinkEntity(link.url())));

        if (subscriptionRepository.existsByChatChatIdAndLinkId(chat.chatId(), linkEntity.getId())) {
            return false;
        }

        SubscriptionEntity subscription = new SubscriptionEntity(
                chatRepository.getReferenceById(chat.chatId()), linkEntity, resolveTags(link.tags()));

        subscriptionRepository.save(subscription);

        return true;
    }

    @Override
    @Transactional
    public Optional<Link> removeLinkFromChat(Chat chat, Link link) {
        Optional<SubscriptionEntity> subscription =
                subscriptionRepository.findByChatChatIdAndLinkUrl(chat.chatId(), link.url());
        if (subscription.isEmpty()) {
            return Optional.empty();
        }

        SubscriptionEntity entity = subscription.orElseThrow();
        Link removed = toDomain(entity);
        Long linkId = entity.getLink().getId();

        subscriptionRepository.delete(entity);
        if (!subscriptionRepository.existsByLinkId(linkId)) {
            linkRepository.deleteById(linkId);
        }

        return Optional.of(removed);
    }

    @Override
    public Optional<Link> findLinkByChat(Chat chat, String url) {
        return subscriptionRepository
                .findByChatChatIdAndLinkUrl(chat.chatId(), url)
                .map(this::toDomain);
    }

    @Override
    public List<Link> getAllLinksByChat(Chat chat, int offset, int limit) {
        return subscriptionRepository
                .findDistinctByChatChatIdOrderByIdAsc(chat.chatId(), PageRequest.of(offset / limit, limit))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<String> getTrackedLinksPage(int offset, int limit) {
        return linkRepository.findTrackedUrls(PageRequest.of(offset / limit, limit));
    }

    @Override
    public List<Chat> getAllChatByLink(Link link, int offset, int limit) {
        return subscriptionRepository
                .findDistinctByLinkUrlOrderByIdAsc(link.url(), PageRequest.of(offset / limit, limit))
                .stream()
                .map(subscription -> new Chat(subscription.getChat().getChatId()))
                .toList();
    }

    @Override
    public Instant getLastUpdateTime(Link link) {
        return linkRepository
                .findByUrl(link.url())
                .map(LinkEntity::getLastUpdatedAt)
                .orElse(null);
    }

    @Override
    @Transactional
    public void setLastUpdateTime(Link link, Instant lastUpdateTime) {
        linkRepository.findByUrl(link.url()).ifPresent(entity -> entity.setLastUpdatedAt(lastUpdateTime));
    }

    private Set<TagEntity> resolveTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Set.of();
        }

        return tags.stream()
                .map(tag -> tag == null ? "" : tag.trim())
                .filter(tag -> !tag.isBlank())
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll)
                .stream()
                .map(tag -> tagRepository
                        .findByName(tag.toString())
                        .orElseGet(() -> tagRepository.save(new TagEntity(tag.toString()))))
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
    }

    private Link toDomain(SubscriptionEntity subscription) {
        List<String> tags =
                subscription.getTags().stream().map(TagEntity::getName).sorted().toList();
        return new Link(subscription.getLink().getUrl(), tags);
    }
}
